package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.CancellationSource;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public class AdminRideDetailsDTO {

    private Long rideId;
    private RouteResponseDTO route;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private DriverResponseDTO driver;
    private List<Long> passengerIds;

    private boolean canceled;
    private CancellationSource cancellationSource;
    private String cancellationReason;

    private BigDecimal price;
    private boolean panicTriggered;

    private List<RatingResponseDTO> ratings;
    private Collection<InconsistencyReportResponseDTO> inconsistencyReports;

    public AdminRideDetailsDTO() { }

    public Long getRideId() { return rideId; }
    public void setRideId(Long rideId) { this.rideId = rideId; }
    public RouteResponseDTO getRoute() { return route; }
    public void setRoute(RouteResponseDTO route) { this.route = route; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public DriverResponseDTO getDriver() { return driver; }
    public void setDriver(DriverResponseDTO driver) { this.driver = driver; }
    public List<Long> getPassengerIds() { return passengerIds; }
    public void setPassengerIds(List<Long> passengerIds) { this.passengerIds = passengerIds; }
    public boolean isCanceled() { return canceled; }
    public void setCanceled(boolean canceled) { this.canceled = canceled; }
    public CancellationSource getCancellationSource() { return cancellationSource; }
    public void setCancellationSource(CancellationSource cancellationSource) { this.cancellationSource = cancellationSource; }
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public boolean isPanicTriggered() { return panicTriggered; }
    public void setPanicTriggered(boolean panicTriggered) { this.panicTriggered = panicTriggered; }
    public List<RatingResponseDTO> getRatings() { return ratings; }
    public void setRatings(List<RatingResponseDTO> ratings) { this.ratings = ratings; }
    public Collection<InconsistencyReportResponseDTO> getInconsistencyReports() { return inconsistencyReports; }
    public void setInconsistencyReports(Collection<InconsistencyReportResponseDTO> inconsistencyReports) { this.inconsistencyReports = inconsistencyReports; }
}


