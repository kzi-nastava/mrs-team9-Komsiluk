package rs.ac.uns.ftn.iss.Komsiluk.dtos.driver;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.ChangeRequestStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class ProfileChangeRequestResponseDTO {

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
    private Long driverId;
    
    public ProfileChangeRequestResponseDTO() {
		super();
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
	
	public Long getDriverId() {
		return driverId;
	}
	
	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}
}
