package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.CancellationSource;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;

public class RideResponseDTO {

	private Long id;
    private RideStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal price;
    private Long routeId;
    private Long driverId;
    private List<Long> passengerIds;
    private String startAddress;
    private String endAddress;
    private List<String> stops;
    private boolean panicTriggered;
    private CancellationSource cancellationSource;
    private String cancellationReason;
    private Long creatorId;
    
    public RideResponseDTO() {
		super();
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

	public Long getRouteId() {
		return routeId;
	}

	public void setRouteId(Long routeId) {
		this.routeId = routeId;
	}

	public Long getDriverId() {
		return driverId;
	}

	public void setDriverId(Long driverId) {
		this.driverId = driverId;
	}

	public List<Long> getPassengerIds() {
		return passengerIds;
	}

	public void setPassengerIds(List<Long> passengerIds) {
		this.passengerIds = passengerIds;
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

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
}
