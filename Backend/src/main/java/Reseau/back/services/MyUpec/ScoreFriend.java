package Reseau.back.services.MyUpec;

import Reseau.back.Counters.AfficheBestAmis;
import Reseau.back.models.MyUpec.Profil;
import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.repositories.MyUpec.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreFriend {

    @Autowired
    private ProfilRepository profilRepository;

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;

    public record CalculScore(long id, String nationalite, String etablissement, BigInteger nombreJaime) {}

    public record CalculScoreResult(long id, String nationalite, String etablissement, long nombreJaime, int score) {}

    public List<CalculScoreResult> meilleursAmis(Long myId) {

        Profil monProfil = profilRepository.findByUtilisateur_IdUtilisateur(myId)
                .orElseThrow(() -> new RuntimeException("Profil introuvable idUtilisateur=" + myId));

        String maNat = monProfil.getNationalite();
        String monEtab = monProfil.getEtablissement();

        List<AfficheBestAmis> amis = demandeAmiRepository.AffichageMeilleureAmis(myId);

        List<CalculScoreResult> resultats = new ArrayList<>();

        for (AfficheBestAmis a : amis) {

            CalculScore row = new CalculScore(
                    a.getAmiId(),
                    a.getNationalite(),
                    a.getEtablissement(),
                    a.getNb_jaime_sur_mes_publications()
            );

            long likes = (row.nombreJaime() == null) ? 0L : row.nombreJaime().longValue();

            int score = 0;
            if (row.etablissement() != null && monEtab != null && row.etablissement().equalsIgnoreCase(monEtab)) score += 10;
            if (row.nationalite() != null && maNat != null && row.nationalite().equalsIgnoreCase(maNat)) score += 5;
            score += (int) likes;

            resultats.add(new CalculScoreResult(row.id(), row.nationalite(), row.etablissement(), likes, score));
        }



        return resultats;
    }
}
