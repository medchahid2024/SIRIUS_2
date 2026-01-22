package Reseau.back.dto;

import java.time.Instant;

public class PublicationRecoDTO {
    private Long idPublication;
    private String contenuTexte;
    private String typePublication;
    private Instant dateCreation;
    private double score;

    public PublicationRecoDTO(Long idPublication, String contenuTexte, String typePublication, Instant dateCreation, double score) {
        this.idPublication = idPublication;
        this.contenuTexte = contenuTexte;
        this.typePublication = typePublication;
        this.dateCreation = dateCreation;
        this.score = score;
    }

    public Long getIdPublication() { return idPublication; }
    public String getContenuTexte() { return contenuTexte; }
    public String getTypePublication() { return typePublication; }
    public Instant getDateCreation() { return dateCreation; }
    public double getScore() { return score; }
}
