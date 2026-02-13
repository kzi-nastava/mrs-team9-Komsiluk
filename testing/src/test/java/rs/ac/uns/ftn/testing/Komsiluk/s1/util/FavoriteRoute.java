package rs.ac.uns.ftn.testing.Komsiluk.s1.util;

import java.util.List;

public class FavoriteRoute {

	private String title;
	private String pickupLocation;
	private int numberOfStations;
	private int numberOfPassengers;
	private String destination;
	private String vehicleType;
	private boolean petFriendly;
	private boolean babyFriendly;
	private List<String> stations;
	private List<String> passengers;
	
	public FavoriteRoute(String title, String pickupLocation, int numberOfStations, int numberOfPassengers, String destination, String vehicleType, boolean petFriendly, boolean babyFriendly, List<String> stations, List<String> passengers) {
		super();
		this.title = title;
		this.pickupLocation = pickupLocation;
		this.numberOfStations = numberOfStations;
		this.numberOfPassengers = numberOfPassengers;
		this.destination = destination;
		this.vehicleType = vehicleType;
		this.petFriendly = petFriendly;
		this.babyFriendly = babyFriendly;
		this.stations = stations;
		this.passengers = passengers;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPickupLocation() {
		return pickupLocation;
	}
	
	public int getNumberOfStations() {
		return numberOfStations;
	}
	
	public int getNumberOfPassengers() {
		return numberOfPassengers;
	}
	
	public String getDestination() {
		return destination;
	}
	
	public String getVehicleType() {
		return vehicleType;
	}
	
	public boolean isPetFriendly() {
		return petFriendly;
	}
	
	public boolean isBabyFriendly() {
		return babyFriendly;
	}
	
	public List<String> getStations() {
		return stations;
	}
	
	public void setStations(List<String> stations) {
		this.stations = stations;
	}
	
	public List<String> getPassengers() {
		return passengers;
	}
	
	public void setPassengers(List<String> passengers) {
		this.passengers = passengers;
	}
}
