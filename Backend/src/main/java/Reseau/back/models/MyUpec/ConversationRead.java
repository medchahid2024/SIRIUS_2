package Reseau.back.models.MyUpec;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(
        name = "conversation_read",
        uniqueConstraints = @UniqueConstraint(columnNames = {"idconversation", "idutilisateur"})
)
public class ConversationRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="idconversation", nullable=false)
    private Long conversationId;

    @Column(name="idutilisateur", nullable=false)
    private Long userId;

    @Column(name="last_read_at")
    private Instant lastReadAt;
}

