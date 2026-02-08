package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.CancellationSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminRideHistoryDTO {

    private Long rideId;

    private String startAddress;
    private String endAddress;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String route;

    private boolean panicTriggered;

    private CancellationSource cancellationSource;

    private String cancellationReason;

    private BigDecimal price;


    public AdminRideHistoryDTO() { }

    public Long getRideId() {
        return rideId;
    }
    public void setRideId(Long rideId) {
        this.rideId = rideId;
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
    public void setPanicTriggered(boolean panicTriggered) {
        this.panicTriggered = panicTriggered;
    }
    public boolean isPanicTriggered() {
        return panicTriggered;
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
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRoute() {
        return route;
    }
    public void setRoute(String route) {
        this.route = route;
    }
}
