package Reseau.back.models.MyUpec;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.List;

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

    @JsonIgnore
    @Column(name = "mot_de_passe", length = 255, nullable = false)
    private String motDePasse;

    @Column(name = "prenom", length = 100, nullable = false)
    private String prenom;

    @Column(name = "dateinscription")
    @Temporal(TemporalType.DATE)
    private Date dateInscription;

    @Column(name = "sexe", columnDefinition = "TEXT")
    private String sexe;

    //  pour éviter la recursion
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToOne(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private Profil profil;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "emetteur", cascade = CascadeType.ALL)
    private List<DemandeAmi> demandesEnvoyees;

    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "recepteur", cascade = CascadeType.ALL)
    private List<DemandeAmi> demandesRecues;
}
