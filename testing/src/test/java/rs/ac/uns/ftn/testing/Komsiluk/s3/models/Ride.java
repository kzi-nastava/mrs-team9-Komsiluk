package rs.ac.uns.ftn.testing.Komsiluk.s3.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ride {

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal price;

    private String panicTriggered;

    private String cancelledBy;

    private String route;

    public Ride(LocalDateTime startTime, LocalDateTime endTime, BigDecimal price, String panicTriggered, String cancelledBy, String route) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.panicTriggered = panicTriggered;
        this.cancelledBy = cancelledBy;
        this.route = route;
    }

    public Ride() {}

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

    public String getPanicTriggered() {
        return panicTriggered;
    }

    public void setPanicTriggered(String panicTriggered) {
        this.panicTriggered = panicTriggered;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

}
