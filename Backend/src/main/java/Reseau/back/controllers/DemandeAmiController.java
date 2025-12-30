package Reseau.back.controllers;

import Reseau.back.repositories.MyUpec.NationaliteCountView;
import Reseau.back.repositories.MyUpec.SexeCountsView;
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
        return ResponseEntity.ok(service.countPourcentageSexeAmisDecider(id));
    }
    @GetMapping("/statistiques/nationalite/{id}")
    public ResponseEntity<List<NationaliteCountView>> getAmisParNationalite(@PathVariable Long id) {
        return ResponseEntity.ok(service.countNationaliteAmisAcceptes(id));
    }
}
