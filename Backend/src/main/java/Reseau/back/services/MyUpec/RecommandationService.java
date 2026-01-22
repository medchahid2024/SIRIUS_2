package Reseau.back.services.MyUpec;

import Reseau.back.models.MyUpec.Publication;
import Reseau.back.repositories.MyUpec.InteractionRepository;
import Reseau.back.repositories.MyUpec.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Reseau.back.dto.PublicationRecoDTO;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
public class RecommandationService {

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    public void afficherScoresV1(Long userId) {

    int wLike = 1;
    int wPartage = 2;
    int wCommentaire = 3;

    List<Object[]> rows = interactionRepository.findUserInteractionsWithTags(userId);

    java.util.Map<String, Integer> tagScores = new java.util.HashMap<>();

    for (Object[] row : rows) {
        String typeInteraction = row[0] != null ? row[0].toString() : null;
        String tag = row[1] != null ? row[1].toString() : null;

        if (typeInteraction == null || tag == null) continue;

        int poids;
        switch (typeInteraction) {
            case "LIKE":
                poids = wLike;
                break;
            case "PARTAGE":
                poids = wPartage;
                break;
            case "COMMENTAIRE":
                poids = wCommentaire;
                break;
            default:
                poids = 0;
        }

        tagScores.put(tag, tagScores.getOrDefault(tag, 0) + poids);
    }

List<Publication> candidates = publicationRepository.findPublicationsNotInteractedByUser(userId);

class LigneReco {
    Publication pub;
    int scoreTag;
    double ageHours;
    double bonusRecence;
    double finalScore;

    LigneReco(Publication pub, int scoreTag, double ageHours, double bonusRecence, double finalScore) {
        this.pub = pub;
        this.scoreTag = scoreTag;
        this.ageHours = ageHours;
        this.bonusRecence = bonusRecence;
        this.finalScore = finalScore;
    }
}

java.util.List<LigneReco> lignes = new java.util.ArrayList<>();

java.time.Instant now = java.time.Instant.now();

for (Publication p : candidates) {
    String tag = p.getTypePublication();
    int scoreTag = (tag != null) ? tagScores.getOrDefault(tag, 0) : 0;

    java.time.Instant dc = p.getDateCreation();
    if (dc == null) dc = now;

    double ageHours = java.time.Duration.between(dc, now).toMinutes() / 60.0;
    if (ageHours < 0) ageHours = 0; 

    double bonusRecence = 1.0 / (1.0 + ageHours);

    double finalScore = scoreTag + bonusRecence;

    lignes.add(new LigneReco(p, scoreTag, ageHours, bonusRecence, finalScore));
}

lignes.sort((a, b) -> {
    int cmp = Double.compare(b.finalScore, a.finalScore);
    if (cmp != 0) return cmp;

    cmp = Double.compare(a.ageHours, b.ageHours);
    if (cmp != 0) return cmp;

    return Long.compare(b.pub.getIdPublication(), a.pub.getIdPublication());
});

int topN = 10;

System.out.println("\n============================================================");
System.out.println("RECO V3 (Score pondéré + récence) | userId=" + userId);
System.out.println("------------------------------------------------------------");
System.out.println("Règles :");
System.out.println("  1) scoreTag(pub) = somme des poids sur le TAG dans l'historique");
System.out.println("     Poids: LIKE=" + wLike + " | PARTAGE=" + wPartage + " | COMMENTAIRE=" + wCommentaire);
System.out.println("  2) ageHours = âge de la publication en heures");
System.out.println("  3) bonusRecence = 1 / (1 + ageHours)");
System.out.println("  4) finalScore = scoreTag + bonusRecence");
System.out.println("------------------------------------------------------------");
System.out.println("Profil utilisateur (scores par TAG) : " + tagScores);
System.out.println("------------------------------------------------------------");
System.out.println("TOP " + topN + " recommandations (triées par finalScore décroissant)");
System.out.println("============================================================\n");

int shown = Math.min(topN, lignes.size());
for (int i = 0; i < shown; i++) {
    LigneReco l = lignes.get(i);
    Publication p = l.pub;

    String titre = p.getContenuTexte();
    if (titre == null) titre = "";
    if (titre.length() > 80) titre = titre.substring(0, 80) + "...";

    String tag = p.getTypePublication();

    System.out.println("[" + (i + 1) + "] " + titre);
    System.out.println("    TAG            : " + tag);
    System.out.println("    scoreTag(TAG)  : " + l.scoreTag + "  (valeur trouvée dans Profil utilisateur)");
    System.out.printf ("    ageHours       : %.2f h  (depuis dateCreation)%n", l.ageHours);
    System.out.printf ("    bonusRecence   : 1/(1+ageHours) = 1/(1+%.2f) = %.4f%n", l.ageHours, l.bonusRecence);
    System.out.printf ("    finalScore     : scoreTag + bonusRecence = %d + %.4f = %.4f%n",
            l.scoreTag, l.bonusRecence, l.finalScore);
    System.out.println("------------------------------------------------------------");
}

System.out.println("\nNote: calcul effectué sur " + lignes.size() + " publications candidates, affichage limité au TOP " + topN + ".");
System.out.println("============================================================\n");

}
public List<PublicationRecoDTO> getRecommendations(Long userId, int offset, int limit) {
    int wLike = 1, wPartage = 2, wCommentaire = 3;

    List<Object[]> rows = interactionRepository.findUserInteractionsWithTags(userId);
    Map<String, Integer> tagScores = new HashMap<>();

    for (Object[] row : rows) {
        String type = row[0] != null ? row[0].toString() : null;
        String tag = row[1] != null ? row[1].toString() : null;
        if (type == null || tag == null) continue;

        int poids;
        switch (type) {
            case "LIKE":        poids = wLike; break;
            case "PARTAGE":     poids = wPartage; break;
            case "COMMENTAIRE": poids = wCommentaire; break;
            default:            poids = 0;
        }
        tagScores.put(tag, tagScores.getOrDefault(tag, 0) + poids);
    }

    List<Publication> candidates = publicationRepository.findPublicationsNotInteractedByUser(userId);

    Instant now = Instant.now();
    class Scored {
        Publication p;
        double finalScore;
        int scoreTag;
        double ageHours;
        Scored(Publication p, double finalScore, int scoreTag, double ageHours) {
            this.p = p; this.finalScore = finalScore; this.scoreTag = scoreTag; this.ageHours = ageHours;
        }
    }

    List<Scored> scored = new ArrayList<>();
    for (Publication p : candidates) {
        String tag = p.getTypePublication();
        int scoreTag = (tag != null) ? tagScores.getOrDefault(tag, 0) : 0;

        Instant dc = p.getDateCreation() != null ? p.getDateCreation() : now;
        double ageHours = Duration.between(dc, now).toMinutes() / 60.0;
        if (ageHours < 0) ageHours = 0;

        double bonusRecence = 1.0 / (1.0 + ageHours);
        double finalScore = scoreTag + bonusRecence;

        scored.add(new Scored(p, finalScore, scoreTag, ageHours));
    }

    scored.sort((a, b) -> {
        int c = Double.compare(b.finalScore, a.finalScore);
        if (c != 0) return c;
        c = Double.compare(a.ageHours, b.ageHours);
        if (c != 0) return c;
        return Long.compare(b.p.getIdPublication(), a.p.getIdPublication());
    });

    int from = Math.max(0, offset);
    int to = Math.min(scored.size(), from + Math.max(0, limit));
    if (from >= scored.size()) return Collections.emptyList();

    List<PublicationRecoDTO> result = new ArrayList<>();
    for (int i = from; i < to; i++) {
        Publication p = scored.get(i).p;
        result.add(new PublicationRecoDTO(
                p.getIdPublication(),
                p.getContenuTexte(),
                p.getTypePublication(),
                p.getDateCreation(),
                scored.get(i).finalScore
        ));
    }

    return result;
}

public void afficherCalculConsole(Long userId) {

    List<Reseau.back.dto.PublicationRecoDTO> recos =
            getRecommendations(userId, 0, Integer.MAX_VALUE);

    System.out.println("\n============================================================");
    System.out.println("CALCUL DES RECOMMANDATIONS (DEBUG CONSOLE)");
    System.out.println("Utilisateur id = " + userId);
    System.out.println("Formule : finalScore = scoreTag + bonusRecence");
    System.out.println("------------------------------------------------------------");

    int shown = Math.min(10, recos.size());

    for (int i = 0; i < shown; i++) {
        var p = recos.get(i);

        String titre = p.getContenuTexte();
        if (titre == null) titre = "";
        if (titre.length() > 80) titre = titre.substring(0, 80) + "...";

        System.out.println("[" + (i + 1) + "] " + titre);
        System.out.println("    TAG        : " + p.getTypePublication());
        System.out.printf ("    SCORE FINAL: %.4f%n", p.getScore());
        System.out.println("------------------------------------------------------------");
    }

    System.out.println("Note: " + recos.size() + " publications évaluées, affichage TOP " + shown);
    System.out.println("============================================================\n");
}


}
