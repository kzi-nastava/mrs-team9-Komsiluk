package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Chat;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.chat.ChatMessageDTO;

@Component
public class ChatMapper {

    public ChatMessageDTO toDTO(Chat message) {
        String type = "UNKNOWN";
        if (message.getSender().getRole() == UserRole.ADMIN) {
            type = "FROM_ADMIN";
        } else {
            type = "FROM_USER";
        }

        ChatMessageDTO dto = new ChatMessageDTO();

        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderEmail(message.getSender().getEmail());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setContent(message.getContent());
        dto.setSentAt(message.getSentAt());
        dto.setType(type);

        return dto;
    }
}