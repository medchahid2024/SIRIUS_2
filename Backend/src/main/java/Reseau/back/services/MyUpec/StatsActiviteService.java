package Reseau.back.services.MyUpec;

import Reseau.back.repositories.MyUpec.StatsActiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsActiviteService {

    @Autowired
    private StatsActiviteRepository statsActiviteRepository;

    public record StatsActivite(
            int nbPublications,
            int nbInteractions,
            int annee,
            int mois
    ) {}

    public StatsActivite getStatsMois(Long userId, int annee, int mois) {
        return new StatsActivite(statsActiviteRepository.countPublicationsMois(userId, annee, mois),
                                 statsActiviteRepository.countInteractionsMois(userId, annee, mois),annee,mois
        );
    }


}