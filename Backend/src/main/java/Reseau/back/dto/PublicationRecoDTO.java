package Reseau.back.dto;

import java.time.Instant;
import java.util.List;

public class PublicationRecoDTO {
    private Long idPublication;
    private String contenuTexte;
    private String typePublication;
    private Instant dateCreation;
    private double score;
    private String mediaURL;

    private long nbLikes;
    private long nbCommentaires;
    private long nbPartages;
    private List<String> commentaires;

    public PublicationRecoDTO(
            Long idPublication,
            String contenuTexte,
            String typePublication,
            Instant dateCreation,
            double score,
            String mediaURL,
            long nbLikes,
            long nbCommentaires,
            long nbPartages,
            List<String> commentaires
    ) {
        this.idPublication = idPublication;
        this.contenuTexte = contenuTexte;
        this.typePublication = typePublication;
        this.dateCreation = dateCreation;
        this.score = score;
        this.mediaURL = mediaURL;
        this.nbLikes = nbLikes;
        this.nbCommentaires = nbCommentaires;
        this.nbPartages = nbPartages;
        this.commentaires = commentaires;
    }

    public Long getIdPublication() { return idPublication; }
    public String getContenuTexte() { return contenuTexte; }
    public String getTypePublication() { return typePublication; }
    public Instant getDateCreation() { return dateCreation; }
    public double getScore() { return score; }
    public String getMediaURL() { return mediaURL; }

    public long getNbLikes() { return nbLikes; }
    public long getNbCommentaires() { return nbCommentaires; }
    public long getNbPartages() { return nbPartages; }
    public List<String> getCommentaires() { return commentaires; }
}