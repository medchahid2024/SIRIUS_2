package Reseau.back.repositories.MyUpec;

import Reseau.back.Counters.AffichageAmis;
import Reseau.back.Counters.NationaliteCountView;
import Reseau.back.Counters.SexeCountsView;
import Reseau.back.models.MyUpec.DemandeAmi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DemandeAmiRepository extends JpaRepository<DemandeAmi, Long> {

    @Query(value = """
 WITH friends AS (
SELECT idemetteur AS u, idrecepteur AS ami FROM demandeami
 WHERE statutdemande = 'ACCEPTEE'
 UNION ALL
 SELECT idrecepteur AS u, idemetteur AS ami
 FROM demandeami
 WHERE statutdemande = 'ACCEPTEE' )

        SELECT
          COUNT(*) FILTER (WHERE u.sexe = 'MASCULIN') AS nbMasculin,
          COUNT(*) FILTER (WHERE u.sexe = 'FEMININ')  AS nbFeminin,
          COUNT(*) FILTER (WHERE u.sexe IS NULL)      AS nbInconnu
                                                                   
        FROM friends f
        JOIN utilisateur u ON u.idutilisateur = f.ami
        WHERE f.u = :idUser
        """, nativeQuery = true)
    SexeCountsView countSexeAmisAcceptes(@Param("idUser") Long idUser);


    @Query(value = """
    SELECT
      p.nationalite AS nationalite,
      COUNT(*)      AS nb
    FROM demandeami d
    JOIN profil p
      ON p.idutilisateur = CASE
         WHEN d.idemetteur = :idUser THEN d.idrecepteur
         ELSE d.idemetteur
      END
    WHERE d.statutdemande = 'ACCEPTEE'
      AND (:idUser IN (d.idemetteur, d.idrecepteur))
    GROUP BY p.nationalite
    ORDER BY nb DESC
    """, nativeQuery = true)
    List<NationaliteCountView> countAmisParNationalite(@Param("idUser") Long idUser);

@Query (value= """
SELECT
  u.idutilisateur AS idutilisateur,
  u.nom AS nom,
  u.prenom AS prenom,
  p.photoprofil AS photo
FROM demandeami d
JOIN utilisateur u
  ON u.idutilisateur = CASE
    WHEN d.idemetteur = :idUser THEN d.idrecepteur
    ELSE d.idemetteur
  END
LEFT JOIN profil p
  ON p.idutilisateur = u.idutilisateur
WHERE d.statutdemande = 'ACCEPTEE'
  AND :idUser IN (d.idemetteur, d.idrecepteur)
ORDER BY u.nom, u.prenom;
""", nativeQuery = true)
    List<AffichageAmis> afficheMesAmis(@Param("idUser") Long idUser);

}
