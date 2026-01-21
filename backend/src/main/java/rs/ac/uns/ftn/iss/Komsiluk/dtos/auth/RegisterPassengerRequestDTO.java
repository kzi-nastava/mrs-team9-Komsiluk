package rs.ac.uns.ftn.iss.Komsiluk.dtos.auth;


import jakarta.validation.constraints.*;

public class RegisterPassengerRequestDTO {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be 2-50 characters")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'-]{1,49}$", message = "First name contains invalid characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be 2-50 characters")
    @Pattern(regexp = "^[\\p{L}][\\p{L}\\s'-]{1,49}$", message = "Last name contains invalid characters")
    private String lastName;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be 5-100 characters")
    private String address;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50, message = "City must be 2-50 characters")
    private String city;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?\\d{7,15}$", message = "Phone must be 7-15 digits, optional + at start")
    private String phoneNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters, with at least 1 letter and 1 number")
    private String password;

    @NotBlank(message = "Repeat password is required")
    private String confirmPassword;

    public RegisterPassengerRequestDTO() {
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
