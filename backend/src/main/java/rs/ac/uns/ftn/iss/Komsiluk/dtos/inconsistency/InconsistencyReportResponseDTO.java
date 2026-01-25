package rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;

import java.time.LocalDateTime;

public class InconsistencyReportResponseDTO {

    private Long id;
    private Long rideId;
    private Long reporterId;
    private UserRole reporterRole;
    private String reporterEmail;
    private String message;
    private LocalDateTime createdAt;

    // Getters and Setters

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRideId() {
        return rideId;
    }

    public void setRideId(Long rideId) {
        this.rideId = rideId;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public UserRole getReporterRole() {
        return reporterRole;
    }

    public void setReporterRole(UserRole reporterRole) {
        this.reporterRole = reporterRole;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

