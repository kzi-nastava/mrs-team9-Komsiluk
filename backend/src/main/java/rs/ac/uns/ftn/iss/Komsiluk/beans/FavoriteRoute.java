package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.util.List;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class FavoriteRoute {

	private Long id;
    private String title;
    private Route route;
    private User user;
    private List<User> passengers;
    private VehicleType vehicleType;
    private boolean petFriendly;
    private boolean babyFriendly;

    public FavoriteRoute() {
		super();
	}
    
    	public FavoriteRoute(Long id, String title, Route route, User user, List<User> passengers, VehicleType vehicleType, boolean petFriendly, boolean babyFriendly) {
		super();
		this.id = id;
		this.title = title;
		this.route = route;
		this.user = user;
		this.passengers = passengers;
		this.vehicleType = vehicleType;
		this.petFriendly = petFriendly;
		this.babyFriendly = babyFriendly;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public List<User> getPassengers() {
		return passengers;
	}
	
	public void setPassengers(List<User> passengers) {
		this.passengers = passengers;
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
