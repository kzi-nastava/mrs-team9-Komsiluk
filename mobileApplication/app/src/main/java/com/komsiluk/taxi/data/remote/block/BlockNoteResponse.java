package com.komsiluk.taxi.data.remote.block;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BlockNoteResponse {

    @Expose
    @SerializedName("id")
    private Long id;

    @Expose
    @SerializedName("blockedUserEmail")
    private String blockedUserEmail;

    @Expose
    @SerializedName("adminEmail")
    private String adminEmail;

    @Expose
    @SerializedName("reason")
    private String reason;

    @Expose
    @SerializedName("createdAt")
    private String createdAt;

    public Long getId() { return id; }
    public String getBlockedUserEmail() { return blockedUserEmail; }
    public String getAdminEmail() { return adminEmail; }
    public String getReason() { return reason; }
    public String getCreatedAt() { return createdAt; }
}
