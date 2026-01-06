package rs.ac.uns.ftn.iss.Komsiluk.dtos.auth;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;

public class LoginResponseDTO {
    private String token;
    private Long id;
    private String email;
    private UserRole role;
    private DriverStatus driverStatus;

    public LoginResponseDTO() { }

    public LoginResponseDTO(String token, Long id, String email, UserRole role, DriverStatus driverStatus) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
        this.driverStatus = driverStatus;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {

        this.email = email;
    }
    public UserRole getRole() {
        return role;
    }
    public void setRole(UserRole role) {
        this.role = role;
    }
    public DriverStatus getDriverStatus() {
        return driverStatus;
    }
    public void setDriverStatus(DriverStatus driverStatus) {
        this.driverStatus = driverStatus;
    }
}
