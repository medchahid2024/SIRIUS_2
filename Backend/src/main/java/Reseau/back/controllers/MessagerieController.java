package Reseau.back.controllers;

import Reseau.back.services.MyUpec.MessagerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/MyUpec/messagerie")
public class   MessagerieController {
    @Autowired
    private Reseau.back.repositories.MyUpec.ConversationReadRepository conversationReadRepository;

    public record ReadReq(Long userId) {}

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long conversationId, @RequestBody ReadReq req) {
        var crOpt = conversationReadRepository.findByConversationIdAndUserId(conversationId, req.userId());
        var cr = crOpt.orElseGet(() -> {
            var x = new Reseau.back.models.MyUpec.ConversationRead();
            x.setConversationId(conversationId);
            x.setUserId(req.userId());
            return x;
        });
        cr.setLastReadAt(java.time.Instant.now());
        conversationReadRepository.save(cr);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread/{userId}")
    public ResponseEntity<Long> totalUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(messagerieService.totalUnread(userId));
    }

    @Autowired
    private Reseau.back.controllers.MessagerieWsController ws;

    @GetMapping("/presence/online")
    public ResponseEntity<java.util.Set<Long>> onlineUsers() {
        return ResponseEntity.ok(ws.getOnlineUsers());
    }

    @Autowired
    private MessagerieService messagerieService;

    @Value("${messagerie.upload.dir:uploads}")
    private String uploadDir;

    public record CreateConversationReq(Long fromUserId, Long toUserId) {}
    public record SendMessageReq(Long senderId, String contenu) {}
    public record CreateGroupReq(String nom, Long creatorId, java.util.List<Long> participantIds) {}
    public record MembreReq(Long requesterId, Long userId) {}

    @PostMapping("/conversations")
    public ResponseEntity<MessagerieService.ConversationDto> createConversation(@RequestBody CreateConversationReq req) {
        return ResponseEntity.ok(messagerieService.createOrGetConversation(req.fromUserId(), req.toUserId()));
    }

    @PostMapping("/groupes")
    public ResponseEntity<MessagerieService.ConversationDto> createGroupe(@RequestBody CreateGroupReq req) {
        return ResponseEntity.ok(messagerieService.createGroup(req.nom(), req.creatorId(), req.participantIds()));
    }

    @PostMapping("/groupes/{convId}/membres")
    public ResponseEntity<MessagerieService.ConversationDto> ajouterMembre(
            @PathVariable Long convId,
            @RequestBody MembreReq req) {
        return ResponseEntity.ok(messagerieService.ajouterMembre(convId, req.requesterId(), req.userId()));
    }

    @DeleteMapping("/groupes/{convId}/quitter")
    public ResponseEntity<Void> quitterGroupe(
            @PathVariable Long convId,
            @RequestParam Long userId) {
        messagerieService.quitterGroupe(convId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/groupes/{convId}/membres/{userId}")
    public ResponseEntity<MessagerieService.ConversationDto> supprimerMembre(
            @PathVariable Long convId,
            @PathVariable Long userId,
            @RequestParam Long requesterId) {
        return ResponseEntity.ok(messagerieService.supprimerMembre(convId, requesterId, userId));
    }

    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<MessagerieService.ConversationDto>> getConversations(@PathVariable Long userId) {
        return ResponseEntity.ok(messagerieService.listConversations(userId));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessagerieService.MessageDto>> getMessages(
            @PathVariable Long conversationId,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(messagerieService.listMessages(conversationId, userId));
    }

    @PostMapping(value = "/conversations/{convId}/fichiers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessagerieService.MessageDto> sendFichier(
            @PathVariable Long convId,
            @RequestParam Long senderId,
            @RequestParam MultipartFile fichier) {
        return ResponseEntity.ok(messagerieService.sendFichier(convId, senderId, fichier));
    }

    @GetMapping("/fichiers/{nom:.+}")
    public ResponseEntity<Resource> getFichier(@PathVariable String nom) {
        try {
            if (nom.contains("..") || nom.contains("/")) return ResponseEntity.badRequest().build();
            Path file = Paths.get(uploadDir).resolve(nom);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) return ResponseEntity.notFound().build();

            String ext = nom.contains(".") ? nom.substring(nom.lastIndexOf(".")).toLowerCase() : "";
            String contentType;
            String disposition;
            if (ext.equals(".png") || ext.equals(".jpg") || ext.equals(".jpeg")) {
                contentType = ext.equals(".png") ? "image/png" : "image/jpeg";
                disposition = "inline";
            } else if (ext.equals(".pdf")) {
                contentType = "application/pdf";
                disposition = "attachment";
            } else {
                contentType = "application/octet-stream";
                disposition = "attachment";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, disposition + "; filename=\"" + nom + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessagerieService.MessageDto> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody SendMessageReq req
    ) {
        return ResponseEntity.ok(messagerieService.sendMessage(conversationId, req.senderId(), req.contenu()));
    }
    @GetMapping("/conversations/{userId}/filtered")
    public ResponseEntity<List<MessagerieService.ConversationDto>> conversationsFiltrees(
            @PathVariable Long userId,
            @RequestParam(required = false) String recherche
    ) {
        List<MessagerieService.ConversationDto> conversations =
                messagerieService.listConversationsFiltered(userId, recherche);

        return ResponseEntity.ok(conversations);
    }

}
