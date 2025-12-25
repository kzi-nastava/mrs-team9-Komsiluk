package rs.ac.uns.ftn.iss.Komsiluk.dtos.driver;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleCreateDTO;

public class DriverCreateDTO {
	
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String phoneNumber;
	private String email;
	private String profileImageUrl;
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
