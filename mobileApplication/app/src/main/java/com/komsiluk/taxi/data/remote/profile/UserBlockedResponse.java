package com.komsiluk.taxi.data.remote.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserBlockedResponse {

    @Expose
    @SerializedName("blocked")
    private boolean blocked;

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
