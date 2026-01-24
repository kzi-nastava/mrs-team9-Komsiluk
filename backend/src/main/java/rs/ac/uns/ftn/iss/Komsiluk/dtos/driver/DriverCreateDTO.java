package rs.ac.uns.ftn.iss.Komsiluk.dtos.driver;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleCreateDTO;

public class DriverCreateDTO {
	
	@NotBlank
	@Size(min = 2, max = 30)
	private String firstName;
	
	@NotBlank
	@Size(min = 2, max = 30)
	private String lastName;
	
	@NotBlank
	@Size(min = 5, max = 100)
	private String address;
	
	@NotBlank
	@Size(min = 2, max = 50)
	private String city;
	
	@NotBlank
	@Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Invalid phone number")
	private String phoneNumber;

	@NotBlank
	@Email
	private String email;
	
	private String profileImageUrl;
	
	@Valid
	@NotNull
	private VehicleCreateDTO vehicle;
	
	public DriverCreateDTO() {
		super();
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public VehicleCreateDTO getVehicle() {
		return vehicle;
	}

	public void setVehicle(VehicleCreateDTO vehicle) {
		this.vehicle = vehicle;
	}
}
