package rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class VehicleCreateDTO {
	
	private String model;
	private VehicleType type;
	private String licencePlate;
	private int seatCount;
	private boolean babyFriendly;
	private boolean petFriendly;
	
	public VehicleCreateDTO() {
		super();
	}
	
	public String getModel() {
		return model;
	}
	
	public void setModel(String model) {
		this.model = model;
	}
	
	public VehicleType getType() {
		return type;
	}
	
	public void setType(VehicleType type) {
		this.type = type;
	}
	
	public String getLicencePlate() {
		return licencePlate;
	}
	
	public void setLicencePlate(String licencePlate) {
		this.licencePlate = licencePlate;
	}
	
	public int getSeatCount() {
		return seatCount;
	}
	
	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
	}
	
	public boolean isBabyFriendly() {
		return babyFriendly;
	}
	
	public void setBabyFriendly(boolean babyFriendly) {
		this.babyFriendly = babyFriendly;
	}
	
	public boolean isPetFriendly() {
		return petFriendly;
	}
	
	public void setPetFriendly(boolean petFriendly) {
		this.petFriendly = petFriendly;
	}

}
