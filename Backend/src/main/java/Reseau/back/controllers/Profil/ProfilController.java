package Reseau.back.controllers.Profil;

import Reseau.back.models.MyUpec.Profil;
import Reseau.back.services.MyUpec.ProfilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/profil")  // ← Slash pour cohérence
public class ProfilController {

    @Autowired
    private ProfilService profilService;

    @GetMapping("/{id}")
    public ResponseEntity<Profil> getProfil(@PathVariable Long id) {
        return profilService.getProfilById(id)
                .map(profil -> new ResponseEntity<>(profil, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    @GetMapping("/all")
    public ResponseEntity<List<Profil>> findAllProfils() {
        return new ResponseEntity<>(profilService.findAllProfils(), HttpStatus.OK);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Long> deleteProfil(@PathVariable Long id) {
        boolean isRemoved = profilService.deleteProfil(id);
        if (!isRemoved) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
