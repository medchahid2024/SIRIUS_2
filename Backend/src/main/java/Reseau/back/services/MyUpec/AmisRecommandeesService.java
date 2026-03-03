package Reseau.back.services.MyUpec;

import Reseau.back.Counters.AmisRecommandees;
import Reseau.back.models.MyUpec.Profil;
import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.repositories.MyUpec.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AmisRecommandeesService {
    public record RecommandationResultat(Long amiId, String nom, String prenom, String nationalite, String etablissement,
                                         String photo, int score) {}

    @Autowired
    private ProfilRepository profilRepository;

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;

    public List<RecommandationResultat> recommanderAmis(Long myId, Long amiId) {
        Profil monProfil = profilRepository.findByUtilisateur_IdUtilisateur(myId)
                .orElseThrow(() -> new RuntimeException("Mon profil est introuvable"));

        String maNat  = monProfil.getNationalite();
        String monEtab = monProfil.getEtablissement();

        List<AmisRecommandees> suggestions = demandeAmiRepository.affichageAmisRecommandees(myId, amiId);

        List<RecommandationResultat> resultats = new ArrayList<>();
        System.out.println("===========================================================");
        System.out.println("               CALCUL DES SCORES DE RECOMMANDATION         ");
        System.out.println("===========================================================");

        System.out.println("-----------------------------------------------------------");
        System.out.println("Bareme :"+ "10 points meme etablissement  5 point si meme nantionalite ");
        System.out.println("Utilisateur connecté :"+myId);


        for (AmisRecommandees suggestion: suggestions) {
            int score = 0;
            System.out.println("monEtab  :" + monEtab+ " ------------- "+"Etablissement de mon amis :"+suggestion.getEtablissement());
            System.out.println("maNat :" + maNat + " ----------------- " + "Nationalite de mon amis :" + suggestion.getNationalite());
            if (monEtab != null && suggestion.getEtablissement() != null
                    && monEtab.equalsIgnoreCase(suggestion.getEtablissement())) {
                score += 10;
                System.out.println(" Même établissement -- +10 points");
            }
            else {
                System.out.println("Etablissement différent -- +0 point");
            }
            if (maNat != null && suggestion.getNationalite() != null
                    && maNat.equalsIgnoreCase(suggestion.getNationalite())) {
                score += 5;
                System.out.println(" Même nationalité -- +5 points");

            }
            else {
                System.out.println(" Nationalité différente -- +0 point");
            }



            System.out.println("------ Score  :"+ score);

            resultats.add(new RecommandationResultat(suggestion.getAmiId(), suggestion.getNom(), suggestion.getPrenom(), suggestion.getNationalite(),
                    suggestion.getEtablissement(),suggestion.getPhoto() ,score
            ));
        }
        resultats.sort(Comparator.comparingInt(RecommandationResultat::score).reversed());

        return resultats;
    }
}