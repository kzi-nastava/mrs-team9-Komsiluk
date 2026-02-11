package com.komsiluk.taxi.data.remote.ride;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PanicRequestDTO {

    @SerializedName("initiatorId")
    @Expose
    private Long initiatorId;

    public PanicRequestDTO() {}

    public Long getInitiatorId() {
        return this.initiatorId;
    }

    public void setInitiatorId(Long id) {
        this.initiatorId = id;
    }
}
