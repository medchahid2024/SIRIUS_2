package Reseau.back.models.MyUpec;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;

@Data
@Entity
@Table(name = "publication")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpublication")
    private Long idPublication;

    @Column(name = "contenutexte", columnDefinition = "TEXT")
    private String contenuTexte;

    @Column(name = "mediaurl", columnDefinition = "TEXT")
    private String mediaURL;

    @Column(name = "datecreation", nullable = false)
    private Instant dateCreation = Instant.now();

    @Column(name = "typepublication", length = 30, nullable = false)
    private String typePublication;

    @Column(name = "visibilite", length = 20, nullable = false)
    private String visibilite;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idutilisateur", referencedColumnName = "idutilisateur", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Utilisateur auteur;
}
