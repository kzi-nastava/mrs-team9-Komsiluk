package rs.ac.uns.ftn.iss.Komsiluk.beans;

import jakarta.persistence.*;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;

import java.time.LocalDateTime;


@Entity
@Table(name = "inconsistency_reports",
        indexes = {
                @Index(name="idx_ir_ride", columnList = "ride_id"),
                @Index(name="idx_ir_reporter", columnList = "reporter_id")
        })
public class InconsistencyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(name = "reporter_role", nullable = false, length = 20)
    private UserRole reporterRole; // DRIVER / PASSENGER

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
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


