package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PanicRequestDTO {

    @NotNull
    @Positive
    private Long initiatorId;


    public PanicRequestDTO() {
    }

    public Long getInitiatorId() {
        return initiatorId;
    }
    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }
}
