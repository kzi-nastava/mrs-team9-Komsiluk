package rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute;

import java.util.List;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class FavoriteRouteResponseDTO {

	private Long id;
    private String title;
    private Long routeId;
    private String startAddress;
    private String endAddress;
    private List<String> stops;
    private List<Long> passengerIds;
    private VehicleType vehicleType;
    private boolean petFriendly;
    private boolean babyFriendly;
    private double distanceKm;
    private Integer estimatedDurationMin;
    
    public FavoriteRouteResponseDTO() {
		super();
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

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public String getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(String startAddress) {
		this.startAddress = startAddress;
	}

	public String getEndAddress() {
		return endAddress;
	}

	public void setEndAddress(String endAddress) {
		this.endAddress = endAddress;
	}

	public List<String> getStops() {
		return stops;
	}

	public void setStops(List<String> stops) {
		this.stops = stops;
	}

	public List<Long> getPassengerIds() {
		return passengerIds;
	}

	public void setPassengerIds(List<Long> passengerIds) {
		this.passengerIds = passengerIds;
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

	public double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}

	public Integer getEstimatedDurationMin() {
		return estimatedDurationMin;
	}

	public void setEstimatedDurationMin(Integer estimatedDurationMin) {
		this.estimatedDurationMin = estimatedDurationMin;
	}
}
