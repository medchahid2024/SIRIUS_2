package Reseau.back.controllers;

import Reseau.back.services.MyUpec.MessagerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    public record CreateConversationReq(Long fromUserId, Long toUserId) {}
    public record SendMessageReq(Long senderId, String contenu) {}

    @PostMapping("/conversations")
    public ResponseEntity<MessagerieService.ConversationDto> createConversation(@RequestBody CreateConversationReq req) {
        return ResponseEntity.ok(messagerieService.createOrGetConversation(req.fromUserId(), req.toUserId()));
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

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessagerieService.MessageDto> sendMessage(
            @PathVariable Long conversationId,
            @RequestBody SendMessageReq req
    ) {
        return ResponseEntity.ok(messagerieService.sendMessage(conversationId, req.senderId(), req.contenu()));
    }
}
