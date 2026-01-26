package com.komsiluk.taxi.data.remote.auth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ResetPasswordRequest {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("newPassword")
    @Expose
    private String newPassword;
    @SerializedName("confirmPassword")
    @Expose
    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}