package rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class FavoriteRouteCreateDTO {

	@NotBlank
	@Size(min = 2, max = 500)
	private String title;
	
	@NotNull
	@Positive
    private Long routeId;
	
	@NotNull
	@Positive
    private Long userId;
	
    private List<@Email String> passengersEmails;
    
    @NotNull
    private VehicleType vehicleType;
    
    private boolean petFriendly;
    private boolean babyFriendly;
   
    public FavoriteRouteCreateDTO() {
        super();
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public List<String> getPassengersEmails() {
		return passengersEmails;
	}
	
	public void setPassengersEmails(List<String> passengersEmails) {
		this.passengersEmails = passengersEmails;
	}
	
	public VehicleType getVehicleType() {
		return vehicleType;
	}
	
	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}
	
	public boolean isPetFriendly() {
		return petFriendly;
	}
	
	public void setPetFriendly(boolean petFriendly) {
		this.petFriendly = petFriendly;
	}
	
	public boolean isBabyFriendly() {
		return babyFriendly;
	}
	
	public void setBabyFriendly(boolean babyFriendly) {
		this.babyFriendly = babyFriendly;
	}
}
