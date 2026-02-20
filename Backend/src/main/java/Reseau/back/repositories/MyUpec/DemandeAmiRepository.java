package Reseau.back.repositories.MyUpec;

import Reseau.back.Counters.*;
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



@Query (value = """
WITH mes_amis AS (
  SELECT DISTINCT
    CASE
      WHEN da.idemetteur = :myId THEN da.idrecepteur
      ELSE da.idemetteur
    END AS ami_id
  FROM demandeami da
  WHERE (da.idemetteur = :myId OR da.idrecepteur = :myId)
    AND da.statutdemande = 'ACCEPTEE'
)
SELECT
   ma.ami_id        AS "amiId",
    pr.etablissement AS "etablissement",
    pr.nationalite   AS "nationalite",
    COUNT(*)         AS "nb_jaime_sur_mes_publications"
FROM mes_amis ma
JOIN profil pr       ON pr.idutilisateur = ma.ami_id
JOIN interaction i   ON i.idutilisateur = ma.ami_id
JOIN publication p   ON p.idpublication = i.idpublication
WHERE p.idutilisateur = :myId
  AND i.typeinteraction = 'LIKE'
GROUP BY ma.ami_id, pr.nationalite, pr.etablissement
ORDER BY nb_jaime_sur_mes_publications DESC LIMIT 10;

""",nativeQuery = true)
List<AfficheBestAmis> AffichageMeilleureAmis(@Param("myId") Long idUser);




    @Query(value = """
WITH amis_de_mon_ami AS (
SELECT DISTINCT
    CASE
     WHEN da.idemetteur = :amiId THEN da.idrecepteur
      ELSE da.idemetteur
     END AS suggestion_id
    FROM demandeami da
    WHERE (da.idemetteur = :amiId OR da.idrecepteur = :amiId)
      AND da.statutdemande = 'ACCEPTEE'
)

SELECT
    ada.suggestion_id  AS amiId,
    pr.etablissement    AS etablissement,
    pr.nationalite      AS nationalite,
  u.nom AS nom,
u.prenom AS prenom
FROM amis_de_mon_ami ada
JOIN profil pr ON pr.idutilisateur = ada.suggestion_id JOIN utilisateur u ON u.idutilisateur = pr.idutilisateur
WHERE ada.suggestion_id != :myId
ORDER BY ada.suggestion_id ASC LIMIT 15
""", nativeQuery = true)
    List<AmisRecommandees> affichageAmisRecommandees(
            @Param("myId") Long myId,
            @Param("amiId") Long amiId
    );


    }

