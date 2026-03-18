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
            int publicationsPrecedent,
            double pourcentagePublications,

            int nbInteractions,
            int interactionsPrecedent,
            double pourcentageInteractions,

            int NombreAmiParMois,
            int amisPrecedent,
            double pourcentageAmis,

            int NombreAmiTotal,
            int annee,
            int mois
    ) {}

    public StatsActivite getStatsMois(Long userId, int annee, int mois) {
        int publications = statsActiviteRepository.countPublicationsMois(userId, annee, mois);
        int interactions = statsActiviteRepository.countInteractionsMois(userId, annee, mois);
        int amisMois = statsActiviteRepository.countAmisAcceptesMois(userId, annee, mois);
        int amisTotal = statsActiviteRepository.countTotalAmis(userId);

         int moisPrecedent = mois - 1;
        int anneePrecedente = annee;
        if (moisPrecedent == 0) {
            moisPrecedent = 12;
            anneePrecedente = annee - 1;
        }
        int publicationsPrecedent = statsActiviteRepository.countPublicationsMois(userId, anneePrecedente, moisPrecedent);
        int interactionsPrecedent = statsActiviteRepository.countInteractionsMois(userId, anneePrecedente, moisPrecedent);
        int amisPrecedent = statsActiviteRepository.countAmisAcceptesMois(userId, anneePrecedente, moisPrecedent);

        double pourcentagePublications = calculerPourcentage(publicationsPrecedent, publications);
        double pourcentageInteractions = calculerPourcentage(interactionsPrecedent, interactions);
        double pourcentageAmis = calculerPourcentage(amisPrecedent, amisMois);
        return new StatsActivite(
                publications,
                publicationsPrecedent,
                pourcentagePublications,

                interactions,
                interactionsPrecedent,
                pourcentageInteractions,

                amisMois,
                amisPrecedent,
                pourcentageAmis,

                amisTotal,

                annee,
                mois
        );
    }
    private double calculerPourcentage(int ancien, int nouveau) {
        if (ancien == 0) {
            return nouveau > 0 ? 100.0 : 0.0;
        }
        return Math.round(((nouveau - ancien) * 100.0 / ancien) * 10) / 10.0;
}}