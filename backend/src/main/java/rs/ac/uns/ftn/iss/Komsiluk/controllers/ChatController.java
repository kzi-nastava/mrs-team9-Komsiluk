package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.chat.ChatInboxDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.chat.ChatMessageDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IChatService;

import java.util.List;

@RestController
public class ChatController {

    @Autowired
    private IChatService chatService;

    @GetMapping("/api/chat/history/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DRIVER', 'PASSENGER')")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getChatHistory(userId));
    }

    @MessageMapping("/chat/send")
    public void processMessage(@Valid @Payload ChatMessageDTO chatMessage) {
        chatService.sendMessage(
                chatMessage.getSenderId(),
                chatMessage.getReceiverId(),
                chatMessage.getContent()
        );
    }

    @GetMapping("/api/chat/inbox")
    public ResponseEntity<List<ChatInboxDTO>> getAdminInbox() {
        return ResponseEntity.ok(chatService.getAdminInbox());
    }

    @PutMapping("/api/chat/read/{userId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long userId) {
        chatService.markAsRead(userId);
        return ResponseEntity.ok().build();
    }
}