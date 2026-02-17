package rs.ac.uns.ftn.iss.Komsiluk.socket.services;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationResponseDTO;

@Component
public class NotificationSocketPublisher {
	
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationSocketPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(String email, NotificationResponseDTO dto) {
    	messagingTemplate.convertAndSendToUser(
				email,
    		    "/queue/notifications",
    		    dto
    		);

    }

    public void sendToAdmins(NotificationResponseDTO dto) {
        messagingTemplate.convertAndSend("/topic/admin/panic", dto);
    }
}
