package Reseau.back.services.MyUpec;

import Reseau.back.Counters.AffichageAmis;
import Reseau.back.dto.PublicationRecoDTO;
import Reseau.back.models.MyUpec.Profil;
import Reseau.back.models.MyUpec.Publication;
import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.repositories.MyUpec.InteractionRepository;
import Reseau.back.repositories.MyUpec.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class RecommandationService {

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;

    @Autowired
    private ProfilService profilService;

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    private List<PublicationRecoDTO> getPersonalRecommendations(Long userId, int limit) {

        final int SCALE = 100;

        Map<String, Integer> tagScores = new HashMap<>();

        List<Object[]> rows = interactionRepository.findUserInteractionsWithTags(userId);

        for (Object[] row : rows) {
            String type = row[0] != null ? row[0].toString() : null;
            String tag = row[1] != null ? row[1].toString() : null;

            if (type == null || tag == null) continue;

            int poids = getInteractionWeight(type);
            tagScores.put(tag, tagScores.getOrDefault(tag, 0) + poids * SCALE);
        }

        try {
            Profil profil = profilService.getMonProfil(userId);
            Set<String> centres = parseCentresInteret(profil.getCentresInteret());

            for (String tag : centres) {
                tagScores.put(tag, tagScores.getOrDefault(tag, 0) + 2 * SCALE);
            }
        } catch (Exception ignored) {}

        List<Long> friendIds = getFriendIds(userId);

        if (!friendIds.isEmpty()) {
            List<Object[]> friendRows = interactionRepository.findUsersInteractionsWithTags(friendIds);

            for (Object[] row : friendRows) {
                String type = row[0] != null ? row[0].toString() : null;
                String tag = row[1] != null ? row[1].toString() : null;

                if (type == null || tag == null) continue;

                int poids = getInteractionWeight(type);
                tagScores.put(tag, tagScores.getOrDefault(tag, 0) + poids * 35);
            }
        }

        List<Publication> candidates = publicationRepository.findPublicationsNotInteractedByUser(userId);
        Instant now = Instant.now();

        class Scored {
            Publication publication;
            double score;
            double ageHours;

            Scored(Publication publication, double score, double ageHours) {
                this.publication = publication;
                this.score = score;
                this.ageHours = ageHours;
            }
        }

        List<Scored> scored = new ArrayList<>();

        for (Publication p : candidates) {
            String tag = p.getTypePublication();

            double scoreTag = tag != null ? tagScores.getOrDefault(tag, 0) / (double) SCALE : 0;
            double ageHours = getAgeHours(p, now);
            double bonusRecence = getRecencyScore(ageHours);

            double mediaBonus = p.getMediaURL() != null && !p.getMediaURL().trim().isEmpty() ? 0.25 : 0;

            double finalScore = scoreTag * 0.75 + bonusRecence * 12 + mediaBonus;

            scored.add(new Scored(p, finalScore, ageHours));
        }

        scored.sort((a, b) -> {
            int compareScore = Double.compare(b.score, a.score);
            if (compareScore != 0) return compareScore;

            int compareAge = Double.compare(a.ageHours, b.ageHours);
            if (compareAge != 0) return compareAge;

            return Long.compare(b.publication.getIdPublication(), a.publication.getIdPublication());
        });

        List<PublicationRecoDTO> result = new ArrayList<>();

        for (int i = 0; i < Math.min(scored.size(), limit); i++) {
            result.add(buildDto(scored.get(i).publication, scored.get(i).score));
        }

        return result;
    }

    private List<PublicationRecoDTO> getSocialRecommendations(Long userId, int limit) {

        List<Long> friendIds = getFriendIds(userId);

        if (friendIds.isEmpty()) return Collections.emptyList();

        List<Object[]> rows = interactionRepository.findFriendPopularPublicationsNotInteracted(userId, friendIds);

        List<PublicationRecoDTO> result = new ArrayList<>();
        Instant now = Instant.now();

        for (int i = 0; i < Math.min(limit, rows.size()); i++) {
            Object[] r = rows.get(i);

            Long pubId = ((Number) r[0]).longValue();
            Publication p = publicationRepository.findPublicationById(pubId).orElse(null);

            if (p == null) continue;

            double nbInteractionsAmis = ((Number) r[1]).doubleValue();
            double ageHours = getAgeHours(p, now);
            double bonusRecence = getRecencyScore(ageHours);
            double mediaBonus = p.getMediaURL() != null && !p.getMediaURL().trim().isEmpty() ? 0.25 : 0;

            double score = nbInteractionsAmis * 1.2 + bonusRecence * 6 + mediaBonus;

            result.add(buildDto(p, score));
        }

        result.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return result;
    }

    public List<PublicationRecoDTO> getRecommendations(Long userId, int offset, int limit) {

        int safeLimit = Math.max(1, limit);

        int personalCount = (int) Math.round(safeLimit * 0.75);
        int socialCount = safeLimit - personalCount;

        List<PublicationRecoDTO> personal = getPersonalRecommendations(userId, Math.max(100, safeLimit * 8));
        List<PublicationRecoDTO> social = getSocialRecommendations(userId, Math.max(50, safeLimit * 4));

        List<PublicationRecoDTO> mixed = new ArrayList<>();
        Set<Long> used = new HashSet<>();

        int ip = 0;
        int is = 0;

        while (mixed.size() < offset + safeLimit * 5 && (ip < personal.size() || is < social.size())) {
            int pos = mixed.size() % safeLimit;
            boolean takePersonal = pos < personalCount;

            PublicationRecoDTO pick = null;

            if (takePersonal) {
                if (ip < personal.size()) pick = personal.get(ip++);
                else if (is < social.size()) pick = social.get(is++);
            } else {
                if (is < social.size()) pick = social.get(is++);
                else if (ip < personal.size()) pick = personal.get(ip++);
            }

            if (pick == null) continue;

            if (used.add(pick.getIdPublication())) {
                mixed.add(pick);
            }
        }

        if (offset >= mixed.size()) return Collections.emptyList();

        int poolFrom = Math.max(0, offset);
        int poolTo = Math.min(mixed.size(), poolFrom + Math.max(50, safeLimit * 10));

        List<PublicationRecoDTO> pool = new ArrayList<>(mixed.subList(poolFrom, poolTo));

        return diversifyFill(pool, safeLimit);
    }

    public void afficherCalculConsole(Long userId) {

        try {
            Profil profil = profilService.getMonProfil(userId);
            Set<String> centres = parseCentresInteret(profil.getCentresInteret());
            List<Long> friendIds = getFriendIds(userId);

            System.out.println("Centres d'interet (profil) = " + centres);
            System.out.println("Nb amis = " + friendIds.size());
        } catch (Exception ignored) {}

        List<PublicationRecoDTO> recos = getRecommendations(userId, 0, 10);

        System.out.println("\n============================================================");
        System.out.println("CALCUL DES RECOMMANDATIONS (DEBUG CONSOLE)");
        System.out.println("Utilisateur id = " + userId);
        System.out.println("------------------------------------------------------------");

        for (int i = 0; i < recos.size(); i++) {
            PublicationRecoDTO p = recos.get(i);

            System.out.println("[" + (i + 1) + "] " + p.getContenuTexte());
            System.out.println("TAG : " + p.getTypePublication());
            System.out.printf("SCORE : %.4f%n", p.getScore());
            System.out.println("-----------------------------------");
        }

        System.out.println("============================================================\n");
    }

    private PublicationRecoDTO buildDto(Publication p, double score) {

        Long publicationId = p.getIdPublication();

        return new PublicationRecoDTO(
                publicationId,
                p.getContenuTexte(),
                p.getTypePublication(),
                p.getDateCreation(),
                score,
                p.getMediaURL(),
                interactionRepository.countByPublicationAndType(publicationId, "LIKE"),
                interactionRepository.countByPublicationAndType(publicationId, "COMMENTAIRE"),
                interactionRepository.countByPublicationAndType(publicationId, "PARTAGE"),
                Collections.emptyList()
        );
    }

    private int getInteractionWeight(String type) {
        switch (type) {
            case "LIKE":
                return 1;
            case "PARTAGE":
                return 2;
            case "COMMENTAIRE":
                return 3;
            default:
                return 0;
        }
    }

    private double getAgeHours(Publication p, Instant now) {
        Instant dateCreation = p.getDateCreation() != null ? p.getDateCreation() : now;

        double ageHours = Duration.between(dateCreation, now).toMinutes() / 60.0;

        if (ageHours < 0) return 0;

        return ageHours;
    }

    private double getRecencyScore(double ageHours) {
        return 1 / (1 + ageHours / 24);
    }

    private List<Long> getFriendIds(Long userId) {

        List<AffichageAmis> amis = demandeAmiRepository.afficheMesAmis(userId);
        List<Long> ids = new ArrayList<>();

        for (AffichageAmis a : amis) {
            if (a == null) continue;

            Long id = a.getIdUtilisateur();

            if (id != null) ids.add(id);
        }

        return ids;
    }

    private Set<String> parseCentresInteret(String raw) {

        Set<String> tags = new HashSet<>();

        if (raw == null) return tags;

        String[] parts = raw.split("[,;|]");

        for (String p : parts) {
            if (p == null) continue;

            String t = p.trim();

            if (!t.isEmpty()) {
                tags.add(t.toUpperCase());
            }
        }

        return tags;
    }

    private List<PublicationRecoDTO> diversifyFill(List<PublicationRecoDTO> pool, int limit) {

        int maxPerTag = Math.max(1, (int) Math.ceil(limit * 0.4));

        Map<String, Integer> tagCount = new HashMap<>();
        List<PublicationRecoDTO> result = new ArrayList<>();
        String lastTag = null;

        for (PublicationRecoDTO p : pool) {
            if (p == null || p.getIdPublication() == null) continue;

            String tag = p.getTypePublication();
            if (tag == null) tag = "UNKNOWN";

            int count = tagCount.getOrDefault(tag, 0);

            if (count >= maxPerTag) continue;
            if (tag.equals(lastTag) && result.size() < limit - 1) continue;

            result.add(p);
            tagCount.put(tag, count + 1);
            lastTag = tag;

            if (result.size() >= limit) return result;
        }

        Set<Long> used = new HashSet<>();
        for (PublicationRecoDTO p : result) {
            used.add(p.getIdPublication());
        }

        for (PublicationRecoDTO p : pool) {
            if (p == null || p.getIdPublication() == null) continue;
            if (used.contains(p.getIdPublication())) continue;

            result.add(p);
            used.add(p.getIdPublication());

            if (result.size() >= limit) break;
        }

        return result;
    }
}