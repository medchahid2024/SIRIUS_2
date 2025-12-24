package Reseau.back.controllers.Profil;

import Reseau.back.models.MyUpec.Profil;
import Reseau.back.repositories.MyUpec.ProfilRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping ("profil")
public class ProfilController {
    @Autowired
    private ProfilRepository profilRepository;

    @PostMapping
 Profil profil(@RequestBody Profil profil){
        log.info("Profil : {}", profil);
    return profilRepository.save(profil);
    }
    @GetMapping
    List<Profil> Profil(){

        return profilRepository.findAll();
    }


}
