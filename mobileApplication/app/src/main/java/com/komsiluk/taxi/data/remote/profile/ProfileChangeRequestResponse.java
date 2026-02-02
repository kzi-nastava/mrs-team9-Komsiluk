package com.komsiluk.taxi.data.remote.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProfileChangeRequestResponse {

    @SerializedName("id") @Expose
    private Long id;

    public Long getId() { return id; }
}
