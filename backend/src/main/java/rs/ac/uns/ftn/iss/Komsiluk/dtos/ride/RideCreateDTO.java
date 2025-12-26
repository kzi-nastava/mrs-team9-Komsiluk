package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import java.time.LocalDateTime;
import java.util.List;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class RideCreateDTO {

	private Long creatorId;
    private String startAddress;
    private String endAddress;
    private List<String> stops;
    private double distanceKm;
    private int estimatedDurationMin;
    private VehicleType vehicleType;
    private boolean babyFriendly;
    private boolean petFriendly;
    private LocalDateTime scheduledAt;
    private List<String> passengerEmails;

    public Long getCreatorId() {
		return creatorId;
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

	public double getDistanceKm() {
		return distanceKm;
	}

	public void setDistanceKm(double distanceKm) {
		this.distanceKm = distanceKm;
	}

	public int getEstimatedDurationMin() {
		return estimatedDurationMin;
	}

	public void setEstimatedDurationMin(int estimatedDurationMin) {
		this.estimatedDurationMin = estimatedDurationMin;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
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

	public LocalDateTime getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(LocalDateTime scheduledAt) {
		this.scheduledAt = scheduledAt;
	}

	public List<String> getPassengerEmails() {
		return passengerEmails;
	}

	public void setPassengerEmails(List<String> passengerEmails) {
		this.passengerEmails = passengerEmails;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
}
