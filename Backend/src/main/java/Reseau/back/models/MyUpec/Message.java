package Reseau.back.models.MyUpec;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idmessage")
    private Long idMessage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idconversation", nullable = false)
    private Conversation conversation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idutilisateur", nullable = false)
    private Utilisateur sender;

    @Column(name = "contenu", columnDefinition = "TEXT", nullable = false)
    private String contenu;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    @PrePersist
    public void prePersist() {
        if (sentAt == null) sentAt = Instant.now();
    }
}

