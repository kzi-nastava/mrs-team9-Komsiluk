package rs.ac.uns.ftn.iss.Komsiluk.dtos.block;

import java.time.LocalDateTime;

public class BlockNoteResponseDTO {
	
    private Long id;
    private String blockedUserEmail;
    private String adminEmail;
    private String reason;
    private LocalDateTime createdAt;

    public BlockNoteResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getBlockedUserEmail() { return blockedUserEmail; }
    public void setBlockedUserEmail(String blockedUserEmail) { this.blockedUserEmail = blockedUserEmail; }

    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}