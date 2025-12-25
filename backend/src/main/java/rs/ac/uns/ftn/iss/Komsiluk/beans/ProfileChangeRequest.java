package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.ChangeRequestStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class ProfileChangeRequest {

	private Long id;
	private LocalDateTime requestedAt;
	private ChangeRequestStatus status;
    private String newName;
    private String newSurname;
    private String newAddress;
    private String newCity;
    private String newPhoneNumber;
    private String newProfileImageUrl;
    private String newModel;
    private VehicleType newType;
    private String newLicencePlate;
    private Integer newSeatCount;
    private Boolean newBabyFriendly;
    private Boolean newPetFriendly;
    private User driver;
    private User admin;
    
    public ProfileChangeRequest() {
		super();
    }
    
	public ProfileChangeRequest(Long id, LocalDateTime requestedAt, ChangeRequestStatus status, String newName,
			String newSurname, String newAddress, String newCity, String newPhoneNumber, String newProfileImageUrl,
			String newModel, VehicleType newType, String newLicencePlate, Integer newSeatCount, Boolean newBabyFriendly,
			Boolean newPetFriendly, User driver, User admin) {
		super();
		this.id = id;
		this.requestedAt = requestedAt;
		this.status = status;
		this.newName = newName;
		this.newSurname = newSurname;
		this.newAddress = newAddress;
		this.newCity = newCity;
		this.newPhoneNumber = newPhoneNumber;
		this.newProfileImageUrl = newProfileImageUrl;
		this.newModel = newModel;
		this.newType = newType;
		this.newLicencePlate = newLicencePlate;
		this.newSeatCount = newSeatCount;
		this.newBabyFriendly = newBabyFriendly;
		this.newPetFriendly = newPetFriendly;
		this.driver = driver;
		this.admin = admin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalDateTime getRequestedAt() {
		return requestedAt;
	}

	public void setRequestedAt(LocalDateTime requestedAt) {
		this.requestedAt = requestedAt;
	}

	public ChangeRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ChangeRequestStatus status) {
		this.status = status;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewSurname() {
		return newSurname;
	}

	public void setNewSurname(String newSurname) {
		this.newSurname = newSurname;
	}

	public String getNewAddress() {
		return newAddress;
	}

	public void setNewAddress(String newAddress) {
		this.newAddress = newAddress;
	}

	public String getNewCity() {
		return newCity;
	}

	public void setNewCity(String newCity) {
		this.newCity = newCity;
	}

	public String getNewPhoneNumber() {
		return newPhoneNumber;
	}

	public void setNewPhoneNumber(String newPhoneNumber) {
		this.newPhoneNumber = newPhoneNumber;
	}

	public String getNewProfileImageUrl() {
		return newProfileImageUrl;
	}

	public void setNewProfileImageUrl(String newProfileImageUrl) {
		this.newProfileImageUrl = newProfileImageUrl;
	}

	public String getNewModel() {
		return newModel;
	}

	public void setNewModel(String newModel) {
		this.newModel = newModel;
	}

	public VehicleType getNewType() {
		return newType;
	}

	public void setNewType(VehicleType newType) {
		this.newType = newType;
	}

	public String getNewLicencePlate() {
		return newLicencePlate;
	}

	public void setNewLicencePlate(String newLicencePlate) {
		this.newLicencePlate = newLicencePlate;
	}

	public Integer getNewSeatCount() {
		return newSeatCount;
	}

	public void setNewSeatCount(Integer newSeatCount) {
		this.newSeatCount = newSeatCount;
	}

	public Boolean getNewBabyFriendly() {
		return newBabyFriendly;
	}

	public void setNewBabyFriendly(Boolean newBabyFriendly) {
		this.newBabyFriendly = newBabyFriendly;
	}

	public Boolean getNewPetFriendly() {
		return newPetFriendly;
	}

	public void setNewPetFriendly(Boolean newPetFriendly) {
		this.newPetFriendly = newPetFriendly;
	}
    
	public User getDriver() {
		return driver;
	}
	
	public void setDriver(User driver) {
		this.driver = driver;
	}
	
	public User getAdmin() {
		return admin;
	}
	
	public void setAdmin(User admin) {
		this.admin = admin;
	}
}
