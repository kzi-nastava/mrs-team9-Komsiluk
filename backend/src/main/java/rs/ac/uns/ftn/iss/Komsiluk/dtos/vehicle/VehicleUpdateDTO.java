package rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class VehicleUpdateDTO {

	@Size(min = 1, max = 50)
    private String model;

    private VehicleType type;

    @Size(min = 3, max = 15)
    @Pattern(regexp = "^$|^[A-Za-z0-9\\-]{3,15}$")
    private String licencePlate;

    @Min(1)
    @Max(8)
    private Integer seatCount;
    
    private Boolean babyFriendly;
    private Boolean petFriendly;
	
	public VehicleUpdateDTO() {
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
