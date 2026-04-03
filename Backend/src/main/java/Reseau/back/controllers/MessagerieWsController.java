package Reseau.back.controllers;

import Reseau.back.services.MyUpec.MessagerieService;
import lombok.Data;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class MessagerieWsController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessagerieService messagerieService;

    private final Map<Long, Integer> onlineCounts = new ConcurrentHashMap<>();

    public MessagerieWsController(SimpMessagingTemplate messagingTemplate, MessagerieService messagerieService) {
        this.messagingTemplate = messagingTemplate;
        this.messagerieService = messagerieService;
    }

    @Data
    public static class PresenceRegister { private Long userId; }

    @Data
    public static class PresenceEvent { private Long userId; private boolean online; }

    @Data
    public static class TypingEvent { private Long userId; private boolean typing; }

    @Data
    public static class StatutConversation { private Long userId; private Long conversationId; private boolean active; }

    @MessageMapping("/presence/register")
    public void registerPresence(PresenceRegister req, SimpMessageHeaderAccessor headers) {
        if (req == null || req.userId == null) return;

        headers.getSessionAttributes().put("userId", req.userId);

        onlineCounts.merge(req.userId, 1, Integer::sum);

        PresenceEvent evt = new PresenceEvent();
        evt.setUserId(req.userId);
        evt.setOnline(true);
        messagingTemplate.convertAndSend("/topic/presence", evt);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Map<String, Object> attrs = sha.getSessionAttributes();
        if (attrs == null) return;

        Object val = attrs.get("userId");
        if (!(val instanceof Long userId)) return;

        onlineCounts.computeIfPresent(userId, (k, v) -> (v <= 1) ? null : (v - 1));

        PresenceEvent evt = new PresenceEvent();
        evt.setUserId(userId);
        evt.setOnline(false);
        messagingTemplate.convertAndSend("/topic/presence", evt);
    }

    @MessageMapping("/conversations/{conversationId}/typing")
    public void typing(@DestinationVariable Long conversationId, TypingEvent req) {
        if (conversationId == null || req == null || req.userId == null) return;
        messagingTemplate.convertAndSend("/topic/conversations/" + conversationId + "/typing", req);
    }

    @MessageMapping("/conversations/{conversationId}/active")
    public void majStatutConversation(@DestinationVariable Long conversationId, StatutConversation req) {
        if (conversationId == null || req == null || req.userId == null) return;
        if (req.active) {
            messagerieService.ajouterActif(conversationId, req.userId);
        } else {
            messagerieService.retirerActif(conversationId, req.userId);
        }
    }

    public Set<Long> getOnlineUsers() {
        return onlineCounts.keySet();
    }
}

