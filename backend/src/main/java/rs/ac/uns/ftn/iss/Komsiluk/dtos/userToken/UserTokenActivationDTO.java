package rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserTokenActivationDTO {

    @NotBlank
    @Size(min = 10, max = 200)
    private String token;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    public UserTokenActivationDTO() { }

    public String getToken() {
        return token;
    }

    public String getPassword() {
        return password;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
