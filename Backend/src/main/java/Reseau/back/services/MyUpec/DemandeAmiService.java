package Reseau.back.services.MyUpec;

import Reseau.back.Counters.AffichageAmis;
import Reseau.back.Counters.AfficheBestAmis;
import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.Counters.NationaliteCountView;
import Reseau.back.Counters.SexeCountsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Service
public class DemandeAmiService {

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;


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
        System.out.println("------ nombre total des amis :"+total);
        System.out.println("---Calcul Homme:" + v.getNbMasculin()+"*100 /"+total );
        System.out.println("---Calcul Femme:" + v.getNbFeminin()+"*100 /"+total);
        System.out.println("---Case vide:" +v.getNbInconnu()+" * 100 /"+total) ;


        System.out.println("HOMME:"+m+"%");
        System.out.println("FEMME:"+f+"%");
        System.out.println("CASE SEXE VIDE:"+inc+"%");

        return new SexePctDto(m,f,inc);

    }




    public List<NationaliteCountView> countNationaliteAmisAcceptes(Long idUser) {
        return demandeAmiRepository.countAmisParNationalite(idUser);
    }

}
