package rs.ac.uns.ftn.iss.Komsiluk.dtos.block;

public class BlockNoteCreateDTO {
	
    private String blockedUserEmail;
    private long adminId;
    private String reason;

    public BlockNoteCreateDTO() {}

    public String getBlockedUserEmail() { return blockedUserEmail; }
    public void setBlockedUserEmail(String blockedUserEmail) { this.blockedUserEmail = blockedUserEmail; }

    public long getAdminId() { return adminId; }
    public void setAdminId(long adminId) { this.adminId = adminId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}