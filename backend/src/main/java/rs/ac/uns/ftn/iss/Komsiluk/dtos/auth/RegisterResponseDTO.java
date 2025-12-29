package rs.ac.uns.ftn.iss.Komsiluk.dtos.auth;

public class RegisterResponseDTO {
    private String message;

    public RegisterResponseDTO() { }

    public RegisterResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
