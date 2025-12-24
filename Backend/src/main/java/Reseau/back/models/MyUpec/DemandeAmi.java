package Reseau.back.models.MyUpec;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "demandeami")
public class DemandeAmi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddemande")
    private Long idDemande;

    @Column(name = "dateenvoi", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateEnvoi;

    @Column(name = "datereponse")
    @Temporal(TemporalType.DATE)
    private Date dateReponse;

    @Enumerated(EnumType.STRING)
    @Column(name = "statutdemande", length = 20, nullable = false)
    private StatutDemande statutDemande;

    @ManyToOne
    @JoinColumn(name = "idemetteur", referencedColumnName = "idutilisateur", nullable = false)
    private Utilisateur emetteur;

    @ManyToOne
    @JoinColumn(name = "idrecepteur", referencedColumnName = "idutilisateur", nullable = false)
    private Utilisateur recepteur;
}
