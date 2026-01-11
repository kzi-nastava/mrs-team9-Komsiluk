package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.CancellationSource;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

@Entity
@Table(name = "rides")
public class Ride {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
    private RideStatus status;
	
	@Column(nullable = false)
    private LocalDateTime createdAt;
	
    private LocalDateTime scheduledAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    @Column(precision = 12, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private boolean panicTriggered;
    
    @Enumerated(EnumType.STRING)
    private CancellationSource cancellationSource;
    
    private String cancellationReason;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    private Route route;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "driver_id")
    private User driver;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ride_passengers", joinColumns = @JoinColumn(name = "ride_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> passengers;
    
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;
    
    @Column(nullable = false)
    private boolean babyFriendly;
    
    @Column(nullable = false)
    private boolean petFriendly;
    
    @Column(nullable = false)
    private double distanceKm;
    
    @Column(nullable = false)
    private int estimatedDurationMin;
    
	public Ride() {
		super();
	}

	public Ride(Long id, RideStatus status, LocalDateTime createdAt, LocalDateTime scheduledAt, LocalDateTime startTime,
			LocalDateTime endTime, BigDecimal price, boolean panicTriggered, CancellationSource cancellationSource,
			String cancellationReason, Route route, User driver, List<User> passengers, User createdBy, VehicleType vehicleType,
			boolean babyFriendly, boolean petFriendly, double distanceKm, int estimatedDurationMin) {
		super();
		this.id = id;
		this.status = status;
		this.createdAt = createdAt;
		this.scheduledAt = scheduledAt;
		this.startTime = startTime;
		this.endTime = endTime;
		this.price = price;
		this.panicTriggered = panicTriggered;
		this.cancellationSource = cancellationSource;
		this.cancellationReason = cancellationReason;
		this.route = route;
		this.driver = driver;
		this.passengers = passengers;
		this.createdBy = createdBy;
		this.vehicleType = vehicleType;
		this.babyFriendly = babyFriendly;
		this.petFriendly = petFriendly;
		this.distanceKm = distanceKm;
		this.estimatedDurationMin = estimatedDurationMin;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RideStatus getStatus() {
		return status;
	}

	public void setStatus(RideStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(LocalDateTime scheduledAt) {
		this.scheduledAt = scheduledAt;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public boolean isPanicTriggered() {
		return panicTriggered;
	}

	public void setPanicTriggered(boolean panicTriggered) {
		this.panicTriggered = panicTriggered;
	}

	public CancellationSource getCancellationSource() {
		return cancellationSource;
	}

	public void setCancellationSource(CancellationSource cancellationSource) {
		this.cancellationSource = cancellationSource;
	}

	public String getCancellationReason() {
		return cancellationReason;
	}

	public void setCancellationReason(String cancellationReason) {
		this.cancellationReason = cancellationReason;
	}

	public Route getRoute() {
		return route;
	}

	public void setRoute(Route route) {
		this.route = route;
	}

	public User getDriver() {
		return driver;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}

	public List<User> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<User> passengers) {
		this.passengers = passengers;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
}
