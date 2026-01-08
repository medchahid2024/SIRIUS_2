package Reseau.back.models.MyUpec;

import jakarta.persistence.*;

import jakarta.persistence.Id;
import java.time.Instant;




    @Entity
    @Table(name = "publication")
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
        private Utilisateur auteur;
    }

