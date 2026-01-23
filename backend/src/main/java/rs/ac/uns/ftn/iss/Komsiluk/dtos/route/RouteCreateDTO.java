package rs.ac.uns.ftn.iss.Komsiluk.dtos.route;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class RouteCreateDTO {

    @NotBlank
    @Size(min = 2, max = 150)
    private String startAddress;

    @NotBlank
    @Size(min = 2, max = 150)
    private String endAddress;

    @Size(max = 2000)
    private String stops;

    @Positive
    private double distanceKm;

    @NotNull
    @Positive
    private Integer estimatedDurationMin;
    
    public RouteCreateDTO() {
		super();
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

	public String getStops() {
		return stops;
	}

	public void setStops(String stops) {
		this.stops = stops;
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
