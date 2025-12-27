package rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken;

public class UserTokenActivationDTO {

	private String token;
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
