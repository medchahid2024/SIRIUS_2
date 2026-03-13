package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsActiviteRepository extends JpaRepository<Utilisateur, Long> {

    @Query(value = """
SELECT COUNT(*) FROM publication
        WHERE idutilisateur = :userId
          AND EXTRACT(YEAR FROM datecreation) = :annee
          AND EXTRACT(MONTH FROM datecreation) = :mois
        """, nativeQuery = true)
    int countPublicationsMois(
            @Param("userId") Long userId,
            @Param("annee") int annee,
            @Param("mois") int mois
    );
}
