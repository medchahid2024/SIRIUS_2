package Reseau.back.services.MyUpec;

import Reseau.back.models.MyUpec.Conversation;
import Reseau.back.models.MyUpec.Message;
import Reseau.back.models.MyUpec.Utilisateur;
import Reseau.back.repositories.MyUpec.ConversationRepository;
import Reseau.back.repositories.MyUpec.MessageRepository;
import Reseau.back.repositories.MyUpec.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessagerieService {

    @Autowired
    private ConversationRepository conversationRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UtilisateurRepository utilisateurRepository;


    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    public record UserMiniDto(Long idUtilisateur, String nom, String prenom) {}


    public record ConversationDto(Long idConversation, UserMiniDto other,
                                  String lastMessage, Instant lastMessageAt,
                                  long unreadCount) {}

    public record MessageDto(Long idMessage, Long conversationId, Long senderId,
                             String senderNom, String senderPrenom, String contenu, Instant sentAt) {}


    @Transactional(readOnly = true)
    public long totalUnread(Long userId) {
        return messageRepository.countTotalUnread(userId);
    }

    @Transactional
    public ConversationDto createOrGetConversation(Long fromUserId, Long toUserId) {
        if (fromUserId == null || toUserId == null) throw new IllegalArgumentException("fromUserId/toUserId requis");
        if (fromUserId.equals(toUserId)) throw new IllegalArgumentException("Impossible de parler à soi-même");

        Long existingId = conversationRepository.findConversationIdBetweenUsers(fromUserId, toUserId);
        if (existingId != null) {
            Conversation c = conversationRepository.findById(existingId)
                    .orElseThrow(() -> new IllegalStateException("Conversation introuvable"));
            return toConversationDto(c, fromUserId);
        }

        Utilisateur u1 = utilisateurRepository.findById(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur fromUserId introuvable"));
        Utilisateur u2 = utilisateurRepository.findById(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur toUserId introuvable"));

        Conversation c = new Conversation();
        c.setParticipants(new ArrayList<>(List.of(u1, u2)));
        c.setCreatedAt(Instant.now());
        c.setUpdatedAt(Instant.now());
        Conversation saved = conversationRepository.save(c);

        return toConversationDto(saved, fromUserId);
    }

    @Transactional(readOnly = true)
    public List<ConversationDto> listConversations(Long userId) {
        return conversationRepository.findByParticipant(userId)
                .stream().map(c -> toConversationDto(c, userId)).toList();
    }

    @Transactional(readOnly = true)
    public List<MessageDto> listMessages(Long conversationId, Long userId) {
        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(conv, userId);

        return messageRepository.findByConversation_IdConversationOrderBySentAtAsc(conversationId)
                .stream().map(this::toMessageDto).toList();
    }

    @Transactional
    public MessageDto sendMessage(Long conversationId, Long senderId, String contenu) {
        if (contenu == null || contenu.trim().isEmpty()) throw new IllegalArgumentException("contenu requis");

        Conversation conv = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation introuvable"));
        ensureParticipant(conv, senderId);

        Utilisateur sender = utilisateurRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        Message m = new Message();
        m.setConversation(conv);
        m.setSender(sender);
        m.setContenu(contenu.trim());
        m.setSentAt(Instant.now());

        Message saved = messageRepository.save(m);

        conv.setUpdatedAt(Instant.now());
        conversationRepository.save(conv);

        MessageDto dto = toMessageDto(saved);


        if (messagingTemplate != null) {
            messagingTemplate.convertAndSend("/topic/conversations/" + conversationId, dto);
        }

        return dto;
    }

    private void ensureParticipant(Conversation conv, Long userId) {
        boolean ok = conv.getParticipants().stream().anyMatch(u -> userId.equals(u.getIdUtilisateur()));
        if (!ok) throw new IllegalArgumentException("Accès refusé (non participant)");
    }

    private ConversationDto toConversationDto(Conversation c, Long viewerId) {
        Utilisateur other = c.getParticipants().stream()
                .filter(u -> !viewerId.equals(u.getIdUtilisateur()))
                .findFirst().orElse(null);

        String last = null;
        Instant lastAt = null;
        var lastMsg = messageRepository.findTop1ByConversation_IdConversationOrderBySentAtDesc(c.getIdConversation());
        if (lastMsg.isPresent()) {
            last = lastMsg.get().getContenu();
            lastAt = lastMsg.get().getSentAt();
        }

        long unread = 0;
        try {
            unread = messageRepository.countUnreadForConversation(c.getIdConversation(), viewerId);
        } catch (Exception ignored) {

            unread = 0;
        }

        UserMiniDto otherDto = (other == null) ? null
                : new UserMiniDto(other.getIdUtilisateur(), other.getNom(), other.getPrenom());

        return new ConversationDto(c.getIdConversation(), otherDto, last, lastAt, unread);
    }

    private MessageDto toMessageDto(Message m) {
        Utilisateur s = m.getSender();
        return new MessageDto(
                m.getIdMessage(),
                m.getConversation().getIdConversation(),
                s.getIdUtilisateur(),
                s.getNom(),
                s.getPrenom(),
                m.getContenu(),
                m.getSentAt()
        );
    }
}
