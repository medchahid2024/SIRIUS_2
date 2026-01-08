package Reseau.back.services.MyUpec;

import Reseau.back.models.MyUpec.Publication;
import Reseau.back.repositories.MyUpec.InteractionRepository;
import Reseau.back.repositories.MyUpec.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RecommandationService {

    @Autowired
    private InteractionRepository interactionRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    public void afficherScoresV1(Long userId) {

        
        List<String> tagsList = interactionRepository.findInteractedPublicationTags(userId);
        Set<String> tags = new HashSet<>(tagsList);

        
        List<Publication> candidates = publicationRepository.findPublicationsNotInteractedByUser(userId);

        System.out.println("==============================================");
        System.out.println("RECO V1 - Utilisateur id = " + userId);
        System.out.println("Tags issus des interactions : " + tags);
        System.out.println("----------------------------------------------");
        System.out.println("ID | TITRE | TAG | SCORE");
        System.out.println("----------------------------------------------");

        for (Publication p : candidates) {
            String tag = p.getTypePublication();
            int score = (tag != null && tags.contains(tag)) ? 1 : 0;

            String titre = p.getContenuTexte();
            if (titre == null) titre = "";
            if (titre.length() > 60) titre = titre.substring(0, 60) + "...";

            System.out.println(p.getIdPublication() + " | " + titre + " | " + tag + " | " + score);
        }

        System.out.println("==============================================");
    }
}
