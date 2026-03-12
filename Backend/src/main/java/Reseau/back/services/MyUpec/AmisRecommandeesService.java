package Reseau.back.services.MyUpec;

import Reseau.back.Counters.AffichageAmis;
import Reseau.back.Counters.AmisRecommandees;
import Reseau.back.models.MyUpec.Profil;
import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.repositories.MyUpec.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AmisRecommandeesService {

    public record RecommandationResultat(
            Long amiId,
            String nom,
            String prenom,
            String nationalite,
            String etablissement,
            String photo,
            int score,
            int amisEnCommun
    ) {}

    @Autowired
    private ProfilRepository profilRepository;

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;

    public List<RecommandationResultat> recommanderAmis(Long myId, Long amiId) {
        Profil monProfil = profilRepository.findByUtilisateur_IdUtilisateur(myId)
                .orElseThrow(() -> new RuntimeException("Mon profil est introuvable"));

        String maNat = monProfil.getNationalite();
        String monEtab = monProfil.getEtablissement();

        List<AffichageAmis> mesAmis = demandeAmiRepository.afficheMesAmis(myId);
        Set<Long> idsMesAmis = mesAmis.stream()
                .map(AffichageAmis::getIdUtilisateur)
                .collect(Collectors.toSet());

        List<AmisRecommandees> suggestions = demandeAmiRepository.affichageAmisRecommandees(myId, amiId);

        List<RecommandationResultat> resultats = new ArrayList<>();

        System.out.println("===========================================================");
        System.out.println("               CALCUL DES SCORES DE RECOMMANDATION         ");
        System.out.println("===========================================================");
        System.out.println("Barème : 10 pts même établissement  5 pts même nationalité  15 pts par ami en commun");
        System.out.println("Utilisateur connecté : " + myId);
        System.out.println("Nombre de mes amis : " + idsMesAmis.size());

        for (AmisRecommandees suggestion : suggestions) {
            int score = 0;
            Long suggestionId = suggestion.getAmiId();

            System.out.println("-- Utilisateur " + suggestion.getPrenom() + " " + suggestion.getNom() + " ---");

            System.out.println("mon etablissement: " + monEtab + "---------"+ "Etablissement de mon amis" + suggestion.getEtablissement());
            if (monEtab != null && suggestion.getEtablissement() != null
                    && monEtab.equalsIgnoreCase(suggestion.getEtablissement())) {
                score += 10;
                System.out.println(" Meme établissement : +10 points");
            } else {
                System.out.println("Etablissements différents : +0 point");
            }

            System.out.println("ma nationalite: " + maNat +"-----"+ "nationalite de mon ami" + suggestion.getNationalite());
            if (maNat != null && suggestion.getNationalite() != null
                    && maNat.equalsIgnoreCase(suggestion.getNationalite())) {
                score += 5;
                System.out.println(" Meme nationalité : +5 points");
            } else {
                System.out.println("Nationalités différentes : +0 point");
            }

            int amisEnCommun = compterAmisEnCommun(suggestionId, idsMesAmis);
            score += amisEnCommun * 15;
            if (amisEnCommun > 0) {
                System.out.println("Amis en commun : " + amisEnCommun + " × 15 = +" + (amisEnCommun * 15) + " points");
            } else {
                System.out.println(" Aucun ami en commun : +0 point");
            }

            System.out.println("------ SCORE TOTAL : " + score + " ------");

            resultats.add(new RecommandationResultat(
                    suggestionId,
                    suggestion.getNom(),
                    suggestion.getPrenom(),
                    suggestion.getNationalite(),
                    suggestion.getEtablissement(),
                    suggestion.getPhoto(),
                    score,
                    amisEnCommun
            ));
        }

        resultats.sort(Comparator.comparingInt(RecommandationResultat::score).reversed());

        System.out.println("===========================================================");
        System.out.println("TOP RECOMMANDATIONS :");
        resultats.stream().limit(10).forEach(r ->
                System.out.println(r.prenom() + " " + r.nom() + "  Score: " + r.score() + " Amis communs: " + r.amisEnCommun())
        );
        System.out.println("===========================================================");

        return resultats;
    }


    private int compterAmisEnCommun(Long suggestionId, Set<Long> mesAmisIds) {
        List<AffichageAmis> amisDeLaSuggestion = demandeAmiRepository.afficheMesAmis(suggestionId);

        return (int) amisDeLaSuggestion.stream()
                .map(AffichageAmis::getIdUtilisateur)
                .filter(mesAmisIds::contains)
                .count();
    }
}