package com.komsiluk.taxi.data.remote.block;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockNoteCreateRequest {

    @Expose
    @SerializedName("blockedUserEmail")
    private String blockedUserEmail;

    @Expose
    @SerializedName("adminId")
    private long adminId;

    @Expose
    @SerializedName("reason")
    private String reason;

    public String getBlockedUserEmail() { return blockedUserEmail; }
    public void setBlockedUserEmail(String blockedUserEmail) { this.blockedUserEmail = blockedUserEmail; }

    public long getAdminId() { return adminId; }
    public void setAdminId(long adminId) { this.adminId = adminId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
