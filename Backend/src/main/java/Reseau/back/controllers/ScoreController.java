package Reseau.back.controllers;


import Reseau.back.services.MyUpec.ScoreFriend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
    @RequestMapping("/mesAmis")
    public class ScoreController {

        @Autowired
        private ScoreFriend scoreFriend;

        @GetMapping("/Meilleures/{id}")
        public ResponseEntity<List<ScoreFriend.CalculScoreResult>> meilleurs(@PathVariable Long id) {
            return ResponseEntity.ok(scoreFriend.meilleursAmis(id));
        }


}
