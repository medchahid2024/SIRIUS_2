package Reseau.back.controllers;

import Reseau.back.services.MyUpec.StatsActiviteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/stats")
public class StatsActiviteController {
    @Autowired
    private StatsActiviteService statsService;

    @GetMapping("/activite/{userId}")
    public ResponseEntity<?> getStats(@PathVariable Long userId,
            @RequestParam(required = false) Integer annee,
            @RequestParam(required = false) Integer mois) {

        return ResponseEntity.ok(statsService.getStatsMois(userId, annee, mois));
    }
}
