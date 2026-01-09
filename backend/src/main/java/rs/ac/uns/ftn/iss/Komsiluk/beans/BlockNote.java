package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

public class BlockNote {

    private Long id;
    private User blockedUser;
    private User admin;
    private String reason;
    private LocalDateTime createdAt;

    public BlockNote() {
    }

    public BlockNote(Long id, User blockedUser, User admin, String reason, LocalDateTime createdAt) {
        this.id = id;
        this.blockedUser = blockedUser;
        this.admin = admin;
        this.reason = reason;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getBlockedUser() { return blockedUser; }
    public void setBlockedUser(User blockedUser) { this.blockedUser = blockedUser; }

    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}