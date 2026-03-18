package Reseau.back.services.MyUpec;

import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.repositories.MyUpec.StatsActiviteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsActiviteService {

    @Autowired
    private StatsActiviteRepository statsActiviteRepository;
    private DemandeAmiRepository demandeAmiRepository;

    public record StatsActivite(
            int nbPublications,
            int nbInteractions,
            int NombreAmiParMois,
            int NombreAmiTotal,
            int annee,
            int mois
    ) {}

    public StatsActivite getStatsMois(Long userId, int annee, int mois) {
        return new StatsActivite(statsActiviteRepository.countPublicationsMois(userId, annee, mois),
                                 statsActiviteRepository.countInteractionsMois(userId, annee, mois),
                statsActiviteRepository.countAmisAcceptesMois(userId, annee, mois),statsActiviteRepository.countTotalAmis(userId), annee,mois
        );
    }


}