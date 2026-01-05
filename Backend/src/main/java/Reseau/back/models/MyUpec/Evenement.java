package Reseau.back.models.MyUpec;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "evenement")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idevenement")
    private Long idEvenement;

    @Column(name = "titre", length = 150, nullable = false)
    private String titre;

    @Column(name = "datedebut", nullable = false)
    private Instant dateDebut;

    @Column(name = "datefin", nullable = false)
    private Instant dateFin;

    @Column(name = "lieu", length = 255)
    private String lieu;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpublication", referencedColumnName = "idpublication", unique = true)
    private Publication publication;
}
