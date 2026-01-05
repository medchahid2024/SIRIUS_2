package Reseau.back.models.MyUpec;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "interaction")
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idinteraction")
    private Long idInteraction;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeinteraction", length = 30, nullable = false)
    private TypeInteraction typeInteraction;


    @Column(name = "contenucommentaire", columnDefinition = "TEXT")
    private String contenuCommentaire;

    @Column(name = "dateinteraction", nullable = false)
    private Instant dateInteraction = Instant.now();

    // qui interagit
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutilisateur", referencedColumnName = "idutilisateur", nullable = false)
    private Utilisateur utilisateur;

    // sur quelle publication
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpublication", referencedColumnName = "idpublication", nullable = false)
    private Publication cible;
}
