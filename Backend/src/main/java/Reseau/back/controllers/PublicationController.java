package Reseau.back.controllers;

import Reseau.back.models.MyUpec.Publication;
import Reseau.back.repositories.MyUpec.PublicationRepository;
import Reseau.back.services.MyUpec.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/MyUpec/publication")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    private PublicationRepository publicationRepository;

    @GetMapping("/{id}")
    public ResponseEntity<Publication> getPublication(@PathVariable Long id) {
        return publicationService.getPublicationById(id)
                .map(publication -> new ResponseEntity<>(publication, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Publication>> findAllPublications() {
        return new ResponseEntity<>(publicationService.findAllPublications(), HttpStatus.OK);
    }

    @GetMapping("/interacted/{userId}")
    public ResponseEntity<List<Publication>> findPublicationsInteractedByUser(@PathVariable Long userId) {
        return new ResponseEntity<>(publicationService.findPublicationsInteractedByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/type/{typePublication}")
    public ResponseEntity<List<Publication>> findPublicationsByType(@PathVariable String typePublication) {
        return new ResponseEntity<>(
                publicationService.findPublicationsByTypePublication(typePublication),
                HttpStatus.OK
        );
    }

    @PostMapping
    Publication publication(@RequestBody Publication publication) {
        return publicationRepository.save(publication);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deletePublication(@PathVariable Long id) {
        boolean isRemoved = publicationService.deletePublication(id);
        if (!isRemoved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
