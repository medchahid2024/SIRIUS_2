package Reseau.back.services.MyUpec;

import Reseau.back.models.MyUpec.Publication;
import Reseau.back.repositories.MyUpec.InteractionRepository;
import Reseau.back.repositories.MyUpec.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.Counters.AffichageAmis;
import Reseau.back.dto.PublicationRecoDTO;
import Reseau.back.models.MyUpec.Profil;

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

private List<PublicationRecoDTO> getPersonalRecommendations(Long userId, int offset, int limit) {

        int wLike = 1, wPartage = 2, wCommentaire = 3;
        int wProfil = 2;

        final int SCALE = 100;

        List<Object[]> rows = interactionRepository.findUserInteractionsWithTags(userId);
        Map<String, Integer> tagScores = new HashMap<>();

        for (Object[] row : rows) {

            String type = row[0] != null ? row[0].toString() : null;
            String tag = row[1] != null ? row[1].toString() : null;

            if (type == null || tag == null) continue;

            int poids;

            switch (type) {
                case "LIKE": poids = wLike; break;
                case "PARTAGE": poids = wPartage; break;
                case "COMMENTAIRE": poids = wCommentaire; break;
                default: poids = 0;
            }

            tagScores.put(tag, tagScores.getOrDefault(tag,0) + poids * SCALE);
        }

        try {

            Profil profil = profilService.getMonProfil(userId);
            Set<String> centres = parseCentresInteret(profil.getCentresInteret());

            for(String tag : centres) {
                tagScores.put(tag, tagScores.getOrDefault(tag,0) + wProfil * SCALE);
            }

        } catch(Exception ignored){}


        List<Publication> candidates = publicationRepository.findPublicationsNotInteractedByUser(userId);

        Instant now = Instant.now();

        class Scored {
            Publication p;
            double score;
            double age;

            Scored(Publication p,double score,double age){
                this.p = p;
                this.score = score;
                this.age = age;
            }
        }

        List<Scored> scored = new ArrayList<>();

        for(Publication p : candidates){

            String tag = p.getTypePublication();

            double scoreTag = tag != null ? tagScores.getOrDefault(tag,0)/(double)SCALE : 0;

            Instant dc = p.getDateCreation()!=null ? p.getDateCreation() : now;

            double ageHours = Duration.between(dc,now).toMinutes()/60.0;

            if(ageHours < 0) ageHours = 0;

            double bonusRecence = 1/(1+ageHours);

            double finalScore = scoreTag*0.8 + bonusRecence*20;

            scored.add(new Scored(p,finalScore,ageHours));
        }

        scored.sort((a,b)->Double.compare(b.score,a.score));

        

        int from = Math.max(0,offset);
        int to = Math.min(scored.size(),from+limit);

        List<PublicationRecoDTO> result = new ArrayList<>();

        for(int i=from;i<to;i++){

            Publication p = scored.get(i).p;

            result.add(new PublicationRecoDTO(
                    p.getIdPublication(),
                    p.getContenuTexte(),
                    p.getTypePublication(),
                    p.getDateCreation(),
                    scored.get(i).score
            ));
        }

        return result;
    }

private List<PublicationRecoDTO> getSocialRecommendations(Long userId,int limit){

        List<Long> friendIds = getFriendIds(userId);

        if(friendIds.isEmpty()) return Collections.emptyList();

        List<Object[]> rows =
                interactionRepository.findFriendPopularPublicationsNotInteracted(userId,friendIds);

        List<PublicationRecoDTO> result = new ArrayList<>();

        Instant now = Instant.now();

        for(int i=0;i<Math.min(limit,rows.size());i++){

            Object[] r = rows.get(i);

            Long pubId = ((Number)r[0]).longValue();

            Publication p = publicationRepository.findPublicationById(pubId).orElse(null);

            if(p==null) continue;

            double nb = ((Number)r[1]).doubleValue();

            Instant dc = p.getDateCreation()!=null ? p.getDateCreation() : now;

            double ageHours = Duration.between(dc,now).toMinutes()/60.0;

            double score = nb + 1/(1+ageHours);

            result.add(new PublicationRecoDTO(
                    p.getIdPublication(),
                    p.getContenuTexte(),
                    p.getTypePublication(),
                    p.getDateCreation(),
                    score
            ));
        }

        result.sort((a,b)->Double.compare(b.getScore(),a.getScore()));

        return result;
    }


    public List<PublicationRecoDTO> getRecommendations(Long userId,int offset,int limit){

        int personalCount = (int)Math.round(limit*0.8);
        int socialCount = limit - personalCount;

        List<PublicationRecoDTO> personal =
                getPersonalRecommendations(userId,0,Math.max(100,limit*5));

        List<PublicationRecoDTO> social =
                getSocialRecommendations(userId,Math.max(50,limit*3));

        List<PublicationRecoDTO> mixed = new ArrayList<>();
        Set<Long> used = new HashSet<>();

        int ip=0,is=0;

        while(mixed.size()<offset+limit && (ip<personal.size()||is<social.size())){

            int pos = mixed.size()%limit;

            boolean takePersonal = pos < personalCount;

            PublicationRecoDTO pick = null;

            if(takePersonal){
                if(ip<personal.size()) pick = personal.get(ip++);
                else if(is<social.size()) pick = social.get(is++);
            }
            else{
                if(is<social.size()) pick = social.get(is++);
                else if(ip<personal.size()) pick = personal.get(ip++);
            }

            if(pick==null) continue;

            if(used.add(pick.getIdPublication()))
                mixed.add(pick);
        }

        int from = Math.max(0, offset);
int to = Math.min(mixed.size(), from + limit);

if (from >= mixed.size()) return Collections.emptyList();

int safeLimit = Math.max(1, limit);

int poolFrom = Math.max(0, offset);
int poolTo = Math.min(mixed.size(), poolFrom + Math.max(50, safeLimit * 10));

if (poolFrom >= mixed.size()) return Collections.emptyList();

List<PublicationRecoDTO> pool = new ArrayList<>(mixed.subList(poolFrom, poolTo));

List<PublicationRecoDTO> diversified = diversifyFill(pool, safeLimit);

return diversified;
    }


    public void afficherCalculConsole(Long userId){

        try{

            Profil profil = profilService.getMonProfil(userId);
            Set<String> centres = parseCentresInteret(profil.getCentresInteret());

            List<Long> friendIds = getFriendIds(userId);

            System.out.println("Centres d'interet (profil) = "+centres);
            System.out.println("Nb amis = "+friendIds.size());

        }catch(Exception ignored){}

        List<PublicationRecoDTO> recos = getRecommendations(userId,0,10);

        System.out.println("\n============================================================");
        System.out.println("CALCUL DES RECOMMANDATIONS (DEBUG CONSOLE)");
        System.out.println("Utilisateur id = "+userId);
        System.out.println("------------------------------------------------------------");

        for(int i=0;i<recos.size();i++){

            PublicationRecoDTO p = recos.get(i);

            System.out.println("["+(i+1)+"] "+p.getContenuTexte());
            System.out.println("TAG : "+p.getTypePublication());
            System.out.printf("SCORE : %.4f%n",p.getScore());
            System.out.println("-----------------------------------");
        }

        System.out.println("============================================================\n");
    }


    private List<Long> getFriendIds(Long userId){

        List<AffichageAmis> amis = demandeAmiRepository.afficheMesAmis(userId);

        List<Long> ids = new ArrayList<>();

        for(AffichageAmis a : amis){

            if(a==null) continue;

            Long id = a.getIdUtilisateur();

            if(id!=null) ids.add(id);
        }

        return ids;
    }

    private Set<String> parseCentresInteret(String raw){

        Set<String> tags = new HashSet<>();

        if(raw==null) return tags;

        String[] parts = raw.split("[,;|]");

        for(String p : parts){

            if(p==null) continue;

            String t = p.trim();

            if(!t.isEmpty())
                tags.add(t.toUpperCase());
        }

        return tags;
    }

    private List<PublicationRecoDTO> diversifyFill(List<PublicationRecoDTO> pool, int limit) {

    int maxPerTag = Math.max(1, (int) Math.ceil(limit * 0.3));

    Map<String, Integer> tagCount = new HashMap<>();
    List<PublicationRecoDTO> result = new ArrayList<>();

    for (PublicationRecoDTO p : pool) {

        if (p == null || p.getIdPublication() == null) continue;

        String tag = p.getTypePublication();
        if (tag == null) tag = "UNKNOWN";

        int count = tagCount.getOrDefault(tag, 0);

        if (count >= maxPerTag) continue;

        result.add(p);
        tagCount.put(tag, count + 1);

        if (result.size() >= limit) return result;
    }

    Set<Long> used = new HashSet<>();
    for (PublicationRecoDTO p : result) used.add(p.getIdPublication());

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