package Reseau.back.services.MyUpec;

import Reseau.back.models.MyUpec.Utilisateur;
import Reseau.back.repositories.MyUpec.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findUtilisateurById(id);
    }


    public List<Utilisateur> findAllUtilisateurs() {
        return utilisateurRepository.findAllUtilisateurs();
    }

    public boolean deleteUtilisateur(Long id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }
    public Optional <Utilisateur> login(String email, String motDePasse) {
        return utilisateurRepository.findEmailPassword(email, motDePasse);
}}
