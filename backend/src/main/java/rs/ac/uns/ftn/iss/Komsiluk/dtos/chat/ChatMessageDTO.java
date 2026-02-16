package rs.ac.uns.ftn.iss.Komsiluk.dtos.chat;
import java.time.LocalDateTime;

public class ChatMessageDTO {
    private Long id;
    private Long senderId;
    private String senderEmail;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private String type;

    public ChatMessageDTO(Long id, Long senderId, String senderEmail, Long receiverId, String content, LocalDateTime sentAt, String type) {
        this.id = id;
        this.senderId = senderId;
        this.senderEmail = senderEmail;
        this.receiverId = receiverId;
        this.content = content;
        this.sentAt = sentAt;
        this.type = type;

    }
    public ChatMessageDTO() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
