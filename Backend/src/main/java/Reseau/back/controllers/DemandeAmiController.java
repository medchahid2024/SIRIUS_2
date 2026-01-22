package Reseau.back.controllers;

import Reseau.back.Counters.AffichageAmis;
import Reseau.back.Counters.AfficheBestAmis;
import Reseau.back.Counters.NationaliteCountView;
import Reseau.back.Counters.SexeCountsView;
import Reseau.back.models.MyUpec.Utilisateur;
import Reseau.back.services.MyUpec.DemandeAmiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/MyUpec/ami")
public class DemandeAmiController {

    @Autowired
    private  DemandeAmiService service;


    @GetMapping("/{id}")
    public ResponseEntity<SexeCountsView> getStatsAmis(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.countSexeAmisAcceptes(id));
    }
    @GetMapping("/statistiques/pourcentage/{id}")
    public ResponseEntity<DemandeAmiService.SexePctDto> getPourcentage(@PathVariable Long id) {
        System.out.println("pourcentage du sexe des amis de l'utilisateur "+id);

        return ResponseEntity.ok(service.countPourcentageSexeAmisDecider(id));
    }
    @GetMapping("/statistiques/nationalite/{id}")
    public ResponseEntity<List<NationaliteCountView>> getAmisParNationalite(@PathVariable Long id) {
        return ResponseEntity.ok(service.countNationaliteAmisAcceptes(id));
    }
    @GetMapping("/mesAmis/{id}")
    public ResponseEntity<List<AffichageAmis>> getMyFriends(@PathVariable Long id) {
        return ResponseEntity.ok(service.affichageAmis(id));
    }
    @GetMapping("/mesAmis/Meilleures/{id}")
    public ResponseEntity<List<AfficheBestAmis>> getMyBestFriends(@PathVariable Long id) {

        return ResponseEntity.ok(service.AffichageMeilleureAmis(id));
    }

}
