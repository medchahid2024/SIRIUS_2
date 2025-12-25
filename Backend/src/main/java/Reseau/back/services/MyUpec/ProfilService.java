package Reseau.back.services.MyUpec;

import Reseau.back.models.MyUpec.Profil;
import Reseau.back.repositories.MyUpec.ProfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProfilService {

    @Autowired
    private ProfilRepository profilRepository;

    public Optional<Profil> getProfilById(Long id) {
        return profilRepository.findProfilById(id) ;
    }

    public List<Profil> findAllProfils() {
        return profilRepository.findAllProfils();
    }


    public boolean deleteProfil(Long id) {
        if (profilRepository.existsById(id)) {
            profilRepository.deleteById(id);
            return true;
        }
        return false;
    }
}