package rs.ac.uns.ftn.iss.Komsiluk.dtos.route;

public class RouteCreateDTO {

    private String startAddress;
    private String endAddress;
    private String stops;
    private double distanceKm;
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
