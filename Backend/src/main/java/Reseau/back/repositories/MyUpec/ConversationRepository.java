package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("""
            select c from Conversation c
            join c.participants p
            where p.idUtilisateur = :userId
            order by c.updatedAt desc
            """)
    List<Conversation> findByParticipant(@Param("userId") Long userId);

    @Query(value = """
            select c.idconversation
            from conversation c
            join conversation_participant cp1
              on cp1.idconversation = c.idconversation and cp1.idutilisateur = :u1
            join conversation_participant cp2
              on cp2.idconversation = c.idconversation and cp2.idutilisateur = :u2
            limit 1
            """, nativeQuery = true)
    Long findConversationIdBetweenUsers(@Param("u1") Long u1, @Param("u2") Long u2);
}
