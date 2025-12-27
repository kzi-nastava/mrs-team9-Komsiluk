package rs.ac.uns.ftn.iss.Komsiluk.beans;

public class Route {

	private Long id;
    private String startAddress;
    private String endAddress;
    private String stops;
    private double distanceKm;
    private Integer estimatedDurationMin;
    
    public Route() {
		super();
    }
    
    public Route(Long id, String startAddress, String endAddress, String stops, double distanceKm, Integer estimatedDurationMin) {
		super();
		this.id = id;
		this.startAddress = startAddress;
		this.endAddress = endAddress;
		this.stops = stops;
		this.distanceKm = distanceKm;
		this.estimatedDurationMin = estimatedDurationMin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
