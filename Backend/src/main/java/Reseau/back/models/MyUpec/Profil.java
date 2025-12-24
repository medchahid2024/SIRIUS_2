package Reseau.back.models.MyUpec;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "profil")
public class Profil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprofil")
    private Long idProfil;

    @Column(name = "photoprofil", length = 255)
    private String photoProfil;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "ville", length = 100)
    private String ville;

    @Column(name = "etablissement", length = 150)
    private String etablissement;

    @Column(name = "nationalite", length = 100)
    private String nationalite;

    @Column(name = "centresinteret", columnDefinition = "TEXT")
    private String centresInteret;

    @OneToOne
    @JoinColumn(name = "idutilisateur", referencedColumnName = "idutilisateur", unique = true)
    private Utilisateur utilisateur;
}
