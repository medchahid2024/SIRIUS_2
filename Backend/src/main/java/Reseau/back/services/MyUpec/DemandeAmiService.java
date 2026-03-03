package Reseau.back.services.MyUpec;

import Reseau.back.Counters.*;
import Reseau.back.models.MyUpec.DemandeAmi;
import Reseau.back.models.MyUpec.StatutDemande;
import Reseau.back.models.MyUpec.Utilisateur;
import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.repositories.MyUpec.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class DemandeAmiService {

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;

public List<AffichageAmis> affichageAmis(Long idUser) {
    return demandeAmiRepository.afficheMesAmis(idUser);

}

    public SexeCountsView countSexeAmisAcceptes(Long idUser) {
        return demandeAmiRepository.countSexeAmisAcceptes(idUser);
    }
    public List<AfficheBestAmis> AffichageMeilleureAmis(Long idUser) {
    return demandeAmiRepository.AffichageMeilleureAmis(idUser);
    }
    public record SexePctDto(long pctMasculin, long pctFeminin, long pctInconnu) {}

    public SexePctDto  countPourcentageSexeAmisDecider(Long idUser) {
        SexeCountsView v = demandeAmiRepository.countSexeAmisAcceptes(idUser);
        long nbM = (v == null || v.getNbMasculin() == null) ? 0 : v.getNbMasculin();
        long nbF = (v == null || v.getNbFeminin()  == null) ? 0 : v.getNbFeminin();
        long nbI = (v == null || v.getNbInconnu()  == null) ? 0 : v.getNbInconnu();
       Long total=nbM+nbF+nbI;
       if (total==0) {
           System.out.println("nombre total des amis egal a zero");
           return null;


       }
        Long m = (v.getNbMasculin()*100)/total;
        Long f = (v.getNbFeminin()*100)/total;
        Long inc = (v.getNbInconnu()*100)/total;
        System.out.println("HOMME:"+m+"%");
        System.out.println("FEMME:"+f+"%");
        System.out.println("CASE SEXE VIDE:"+inc+"%");
        return new SexePctDto(m,f,inc);

    }

    public boolean demandeExiste(Long id1, Long id2) {
        return demandeAmiRepository.demandeEnAttenteExiste(id1, id2);
    }


    public List<NationaliteCountView> countNationaliteAmisAcceptes(Long idUser) {
    return demandeAmiRepository.countAmisParNationalite(idUser);
    }

    @Transactional
    public void envoyerDemandeAmi(Long myId, Long amiId) {
        demandeAmiRepository.envoyerDemandeAmi(myId, amiId);
}

    @Transactional
    public void accepterDemandeAmi(Long myId, Long amiId) {
    int lignesModifiees = demandeAmiRepository.accepterDemandeAmi(myId, amiId);
        if (lignesModifiees == 0) {
            throw new RuntimeException("La demande n'a pas pu être acceptée");
        }





    }






//    public List<AmisRecommandees> getSuggestions(Long myId, Long amiId) {
//        if (myId.equals(amiId)) {
//            throw new IllegalArgumentException("Impossible d'afficher la liste recommandee de cet ami.");
//        }
//        return demandeAmiRepository.affichageAmisRecommandees(myId, amiId);
   // }
}
