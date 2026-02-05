package com.komsiluk.taxi.data.remote.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserChangePasswordRequest {

    @SerializedName("oldPassword") @Expose
    private final String oldPassword;

    @SerializedName("newPassword") @Expose
    private final String newPassword;

    public UserChangePasswordRequest(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() { return oldPassword; }
    public String getNewPassword() { return newPassword; }
}
