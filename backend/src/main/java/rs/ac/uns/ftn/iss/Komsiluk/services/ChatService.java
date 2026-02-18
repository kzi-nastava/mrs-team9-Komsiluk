package rs.ac.uns.ftn.iss.Komsiluk.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Chat;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.chat.ChatInboxDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.chat.ChatMessageDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.ChatMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.ChatRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IChatService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService implements IChatService {

    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public List<ChatMessageDTO> getChatHistory(Long userId) {
        return chatRepository.findByConversationIdOrderBySentAtAsc(userId)
                .stream()
                .map(chatMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageDTO sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("Receiver not found"));

        Long conversationId = (sender.getRole() == UserRole.ADMIN) ? receiverId : senderId;

        Chat message = new Chat();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        message.setConversationId(conversationId);
        if (sender.getRole() == UserRole.ADMIN) {
            message.setType("FROM_ADMIN");
        } else {
            message.setType("FROM_USER");
        }

        Chat saved = chatRepository.save(message);
        ChatMessageDTO dto = chatMapper.toDTO(saved);

        messagingTemplate.convertAndSendToUser(
                receiver.getEmail(),
                "/queue/messages",
                dto
        );

        messagingTemplate.convertAndSendToUser(
                sender.getEmail(),
                "/queue/messages",
                dto
        );

        return dto;
    }
    @Override
    public List<ChatInboxDTO> getAdminInbox() {
        List<Chat> latestMessages = chatRepository.findLatestMessagesPerConversation();

        return latestMessages.stream().map(msg -> {
                    Long userId = msg.getConversationId();
                    User user = userRepository.findById(userId).orElse(null);

                    String fullName = "Unknown";
                    String email = "Unknown";
                    String profilePicture = null;

                    if (user != null) {
                        fullName = user.getFirstName() + " " + user.getLastName();
                        email = user.getEmail();
                        profilePicture = user.getProfileImageUrl();
                    }

                    long unreadCount = chatRepository.countUnreadMessages(userId);

                    return new ChatInboxDTO(
                            userId,
                            email,
                            fullName,
                            msg.getContent(),
                            msg.getSentAt(),
                            profilePicture,
                            (int) unreadCount
                    );
                }).sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .collect(Collectors.toList());
    }

    public void markAsRead(Long userId) {
        chatRepository.markAllAsRead(userId);
    }
}