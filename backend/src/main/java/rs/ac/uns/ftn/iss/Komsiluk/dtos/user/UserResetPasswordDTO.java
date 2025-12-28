package rs.ac.uns.ftn.iss.Komsiluk.dtos.user;

public class UserResetPasswordDTO {

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

