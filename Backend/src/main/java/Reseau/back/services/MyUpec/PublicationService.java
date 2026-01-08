package Reseau.back.services.MyUpec;

import Reseau.back.models.MyUpec.Publication;
import Reseau.back.repositories.MyUpec.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PublicationService {

    @Autowired
    private PublicationRepository publicationRepository;

    public Optional<Publication> getPublicationById(Long id) {
        return publicationRepository.findPublicationById(id);
    }

    public List<Publication> findAllPublications() {
        return publicationRepository.findAllPublications();
    }

    public List<Publication> findPublicationsByTypePublication(String typePublication) {
        return publicationRepository.findPublicationsByTypePublication(typePublication);
    }

    public List<Publication> findPublicationsInteractedByUser(Long userId) {
        return publicationRepository.findPublicationsInteractedByUser(userId);
    }

    public boolean deletePublication(Long id) {
        if (publicationRepository.existsById(id)) {
            publicationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
