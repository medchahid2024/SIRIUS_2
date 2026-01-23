package Reseau.back.controllers;

import Reseau.back.dto.PublicationRecoDTO;
import Reseau.back.services.MyUpec.RecommandationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    @Autowired
    private RecommandationService recommandationService;

    @GetMapping("/{userId}")
public List<PublicationRecoDTO> getFeed(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "10") int limit
) {
    if (offset == 0) {
        recommandationService.afficherCalculConsole(userId);
    }

    return recommandationService.getRecommendations(userId, offset, limit);
}
}
