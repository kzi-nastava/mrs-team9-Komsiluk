package rs.ac.uns.ftn.iss.Komsiluk.dtos.user;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleResponseDTO;

public class UserProfileResponseDTO {

	private String email;
	private String firstName;
	private String lastName;
	private String address;
	private String city;
	private String phoneNumber;
	private String profileImageUrl;
	private VehicleResponseDTO vehicle;
	private long activeMinutesLast24h;
	private DriverStatus driverStatus;
	
	public UserProfileResponseDTO() {
		super();
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
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
	
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	
	public VehicleResponseDTO getVehicle() {
		return vehicle;
	}
	
	public void setVehicle(VehicleResponseDTO vehicle) {
		this.vehicle = vehicle;
	}
	
	public long getActiveMinutesLast24h() {
		return activeMinutesLast24h;
	}
	
	public void setActiveMinutesLast24h(long activeMinutesLast24h) {
		this.activeMinutesLast24h = activeMinutesLast24h;
	}
	
	public DriverStatus getDriverStatus() {
		return driverStatus;
	}
	
	public void setDriverStatus(DriverStatus driverStatus) {
		this.driverStatus = driverStatus;
	}
}
