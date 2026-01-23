package rs.ac.uns.ftn.iss.Komsiluk.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserChangePasswordDTO {

    @NotBlank
    @Size(min = 8, max = 72)
    private String oldPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    private String newPassword;

	public UserChangePasswordDTO() {
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
}
