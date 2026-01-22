package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByConversation_IdConversationOrderBySentAtAsc(Long idConversation);

    Optional<Message> findTop1ByConversation_IdConversationOrderBySentAtDesc(Long idConversation);

    /**
     * Compte les messages non lus pour une conversation.
     * Requiert la table conversation_read (conversationId, userId, last_read_at).
     *
     * ⚠️ Si tes noms de colonnes diffèrent (sent_at / idutilisateur / idconversation),
     * adapte la requête.
     */
    @Query(value = """
        select count(*)
        from message m
        left join conversation_read cr
          on cr.idconversation = m.idconversation
         and cr.idutilisateur = :userId
        where m.idconversation = :conversationId
          and m.idutilisateur <> :userId
          and (cr.last_read_at is null or m.sent_at > cr.last_read_at)
    """, nativeQuery = true)
    long countUnreadForConversation(@Param("conversationId") Long conversationId,
                                    @Param("userId") Long userId);

    /**
     * Compte tous les messages non lus (toutes conversations) pour un user.
     */
    @Query(value = """
        select count(*)
        from message m
        join conversation_participant cp
          on cp.idconversation = m.idconversation
         and cp.idutilisateur = :userId
        left join conversation_read cr
          on cr.idconversation = m.idconversation
         and cr.idutilisateur = :userId
        where m.idutilisateur <> :userId
          and (cr.last_read_at is null or m.sent_at > cr.last_read_at)
    """, nativeQuery = true)
    long countTotalUnread(@Param("userId") Long userId);
}

