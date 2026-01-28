package rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InconsistencyReportCreateDTO {
    @NotBlank
    @Size(min = 1, max = 200)
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


