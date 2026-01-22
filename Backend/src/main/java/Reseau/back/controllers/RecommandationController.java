package Reseau.back.controllers;

import Reseau.back.services.MyUpec.RecommandationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/MyUpec/reco")
public class RecommandationController {

    @Autowired
    private RecommandationService recommandationService;

    @GetMapping("/v1/{userId}")
    public ResponseEntity<String> lancerRecoV1(@PathVariable Long userId) {
        recommandationService.afficherScoresV1(userId);
        return ResponseEntity.ok("Reco V1 lancée (voir console backend) pour userId=" + userId);
    }
}
