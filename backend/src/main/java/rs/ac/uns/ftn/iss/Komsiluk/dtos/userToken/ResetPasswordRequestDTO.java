package rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken;

public class ResetPasswordRequestDTO {

    private String token;
    private String newPassword;
    private String confirmPassword;

    public String getToken() {
        return token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
