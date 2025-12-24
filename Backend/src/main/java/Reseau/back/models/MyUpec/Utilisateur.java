package Reseau.back.models.MyUpec;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutilisateur")
    private Long idUtilisateur;

    @Column(name = "nom", length = 100, nullable = false)
    private String nom;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe", length = 255, nullable = false)
    private String motDePasse;

    @Column(name = "dateinscription")
    @Temporal(TemporalType.DATE)
    private Date dateInscription;

    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Profil profil;

    @OneToMany(mappedBy = "emetteur", cascade = CascadeType.ALL)
    private java.util.List<DemandeAmi> demandesEnvoyees;

    @OneToMany(mappedBy = "recepteur", cascade = CascadeType.ALL)
    private java.util.List<DemandeAmi> demandesRecues;
}
