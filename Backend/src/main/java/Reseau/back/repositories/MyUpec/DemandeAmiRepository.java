package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.DemandeAmi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
