package rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken;

import jakarta.validation.constraints.NotBlank;

public class UserTokenPassengerActivationDTO {

    @NotBlank( message = "Token is required")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

