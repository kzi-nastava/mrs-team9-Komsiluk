package rs.ac.uns.ftn.iss.Komsiluk.dtos.push;

import jakarta.validation.constraints.NotBlank;

public class PushTokenRegisterDTO {

    @NotBlank
    private String token;

    public PushTokenRegisterDTO() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
