package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public class RideCreateDTO {

	@NotNull
    @Positive
    private Long creatorId;

    @NotBlank
    @Size(min = 2, max = 150)
    private String startAddress;

    @NotBlank
    @Size(min = 2, max = 150)
    private String endAddress;

    @Size(max = 10)
    private List<@NotBlank @Size(min = 2, max = 150) String> stops;

    @Positive
    private double distanceKm;

    @Positive
    private int estimatedDurationMin;
    
    @NotNull
    private Double startLat;
    
    @NotNull
    private Double startLng;

    @NotNull
    private VehicleType vehicleType;

    private boolean babyFriendly;
    private boolean petFriendly;

    private LocalDateTime scheduledAt;

    private List<@Email String> passengerEmails;

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
	
	public double getStartLat() {
		return startLat;
	}
	
	public void setStartLat(double startLat) {
		this.startLat = startLat;
	}
	
	public double getStartLng() {
		return startLng;
	}
	
	public void setStartLng(double startLng) {
		this.startLng = startLng;
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
