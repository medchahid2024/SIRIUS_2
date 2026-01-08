package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    @Query(value = "SELECT * FROM publication", nativeQuery = true)
    List<Publication> findAllPublications();

    @Query(value = "SELECT * FROM publication WHERE idpublication = :id", nativeQuery = true)
    Optional<Publication> findPublicationById(@Param("id") Long id);

    @Query(value = "SELECT * FROM publication WHERE typepublication = :type", nativeQuery = true)
    List<Publication> findPublicationsByTypePublication(@Param("type") String typePublication);

    @Query(
        value = """
            SELECT DISTINCT p.*
            FROM publication p
            INNER JOIN interaction i ON i.idpublication = p.idpublication
            WHERE i.idutilisateur = :userId
        """,
        nativeQuery = true
    )
    List<Publication> findPublicationsInteractedByUser(@Param("userId") Long userId);

    @Query(
        value = """
            SELECT p.*
            FROM publication p
            WHERE p.idpublication NOT IN (
                SELECT DISTINCT i.idpublication
                FROM interaction i
                WHERE i.idutilisateur = :userId
            )
        """,
        nativeQuery = true
    )
    List<Publication> findPublicationsNotInteractedByUser(@Param("userId") Long userId);
}
