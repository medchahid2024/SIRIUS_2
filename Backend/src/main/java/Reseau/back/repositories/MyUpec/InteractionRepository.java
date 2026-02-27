package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    @Query(
        value = """
            SELECT DISTINCT p.typepublication
            FROM publication p
            INNER JOIN interaction i ON i.idpublication = p.idpublication
            WHERE i.idutilisateur = :userId
        """,
        nativeQuery = true
    )
    List<String> findInteractedPublicationTags(@Param("userId") Long userId);

    @Query(
        value = """
            SELECT DISTINCT i.idpublication
            FROM interaction i
            WHERE i.idutilisateur = :userId
        """,
        nativeQuery = true
    )
    List<Long> findInteractedPublicationIds(@Param("userId") Long userId);

    @Query(
        value = """
            SELECT i.typeinteraction, p.typepublication
            FROM interaction i
            JOIN publication p ON p.idpublication = i.idpublication
            WHERE i.idutilisateur = :userId
        """,
        nativeQuery = true
    )
    List<Object[]> findUserInteractionsWithTags(@Param("userId") Long userId);
   
    @Query(
    value = """
        SELECT i.typeinteraction, p.typepublication
        FROM interaction i
        JOIN publication p ON p.idpublication = i.idpublication
        WHERE i.idutilisateur IN (:userIds)
    """,
    nativeQuery = true
)
List<Object[]> findUsersInteractionsWithTags(@Param("userIds") List<Long> userIds);

@Query(
    value = """
        SELECT i.idpublication, COUNT(*) AS nb
        FROM interaction i
        WHERE i.idutilisateur IN (:friendIds)
          AND i.idpublication NOT IN (
              SELECT DISTINCT i2.idpublication
              FROM interaction i2
              WHERE i2.idutilisateur = :userId
          )
        GROUP BY i.idpublication
        ORDER BY nb DESC
        LIMIT 500
    """,
    nativeQuery = true
)
List<Object[]> findFriendPopularPublicationsNotInteracted(
        @Param("userId") Long userId,
        @Param("friendIds") List<Long> friendIds
);



}
