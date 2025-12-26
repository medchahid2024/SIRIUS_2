package Reseau.back.services.MyUpec;

import Reseau.back.repositories.MyUpec.DemandeAmiRepository;
import Reseau.back.repositories.MyUpec.SexeCountsView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DemandeAmiService {

    @Autowired
    private DemandeAmiRepository demandeAmiRepository;




    public SexeCountsView countSexeAmisAcceptes(Long idUser) {
        return demandeAmiRepository.countSexeAmisAcceptes(idUser);
    }
}
