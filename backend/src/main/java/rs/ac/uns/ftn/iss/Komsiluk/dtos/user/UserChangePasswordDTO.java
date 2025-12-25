package rs.ac.uns.ftn.iss.Komsiluk.dtos.user;

public class UserChangePasswordDTO {

	private String oldPassword;
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
