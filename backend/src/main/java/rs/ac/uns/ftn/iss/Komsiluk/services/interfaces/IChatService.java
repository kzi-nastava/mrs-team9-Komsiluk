package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.chat.ChatInboxDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.chat.ChatMessageDTO;
import java.util.List;

public interface IChatService {

    List<ChatMessageDTO> getChatHistory(Long userId);

    ChatMessageDTO sendMessage(Long senderId, Long receiverId, String content);

    List<ChatInboxDTO> getAdminInbox();
    void markAsRead(Long userId);
}