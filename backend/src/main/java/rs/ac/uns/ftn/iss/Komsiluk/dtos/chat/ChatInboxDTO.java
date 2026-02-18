package rs.ac.uns.ftn.iss.Komsiluk.dtos.chat;

import java.time.LocalDateTime;

public class ChatInboxDTO {
    private Long userId;
    private String email;
    private String fullName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private String profilePicture;
    private int unreadCount;

    public ChatInboxDTO() {}

    public ChatInboxDTO(Long userId, String email, String fullName, String lastMessage, LocalDateTime lastMessageTime, String profilePicture, int unreadCount) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
        this.profilePicture = profilePicture;
        this.unreadCount = unreadCount;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}