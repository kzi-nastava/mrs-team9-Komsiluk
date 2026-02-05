package rs.ac.uns.ftn.iss.Komsiluk.beans;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "routes")
public class Route {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
    private String startAddress;
	
	@Column(nullable = false)
    private String endAddress;
	
    private String stops;
    
    @Column(nullable = false)
    private double distanceKm;
    
    @Column(nullable = false)
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

    public String toFormattedString() {
        if (this.stops == null || this.stops.trim().isEmpty()) {
            return "";
        }

        String[] stopSegments = this.stops.split("\\|");
        StringBuilder formattedStops = new StringBuilder();

        for (int i = 0; i < stopSegments.length; i++) {
            String street = extractStreetAddress(stopSegments[i]);

            if (!street.isEmpty()) {
                formattedStops.append(street);

                if (i < stopSegments.length - 1) {
                    formattedStops.append(",");
                }
            }
        }

        return formattedStops.toString();
    }

    private String extractStreetAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.trim().isEmpty()) {
            return "";
        }
        return fullAddress.split(",")[0].trim();
    }
}
