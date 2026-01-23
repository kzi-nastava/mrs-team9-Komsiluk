package rs.ac.uns.ftn.iss.Komsiluk.dtos.block;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BlockNoteCreateDTO {
	
	@NotBlank
	@Email
    private String blockedUserEmail;
	
	@Positive
    private long adminId;
	
	@NotBlank
	@Size(min = 5, max = 500)
    private String reason;

    public BlockNoteCreateDTO() {}

    public String getBlockedUserEmail() { return blockedUserEmail; }
    public void setBlockedUserEmail(String blockedUserEmail) { this.blockedUserEmail = blockedUserEmail; }

    public long getAdminId() { return adminId; }
    public void setAdminId(long adminId) { this.adminId = adminId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}