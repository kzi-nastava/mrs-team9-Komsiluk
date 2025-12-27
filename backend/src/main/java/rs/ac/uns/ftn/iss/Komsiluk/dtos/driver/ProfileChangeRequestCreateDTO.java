package rs.ac.uns.ftn.iss.Komsiluk.dtos.driver;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class ProfileChangeRequestCreateDTO {

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
    
    public ProfileChangeRequestCreateDTO() {
		super();
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
}
