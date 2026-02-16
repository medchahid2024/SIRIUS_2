package Reseau.back.repositories.MyUpec;

import Reseau.back.models.MyUpec.ConversationRead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationReadRepository extends JpaRepository<ConversationRead, Long> {
    Optional<ConversationRead> findByConversationIdAndUserId(Long conversationId, Long userId);
}