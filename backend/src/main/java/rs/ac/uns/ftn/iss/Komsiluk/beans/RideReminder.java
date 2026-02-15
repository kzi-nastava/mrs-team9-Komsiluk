package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "ride_reminder",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_ride_user_slot",
            columnNames = {"ride_id", "user_id", "slot_time"}
        )
    }
)
public class RideReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long rideId;

    @Column(nullable=false)
    private Long userId;

    @Column(nullable=false)
    private LocalDateTime slotTime;

    @Column(nullable=false)
    private boolean sent = false;

    private LocalDateTime sentAt;

    public RideReminder() {
		super();
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
	
	public Long getUserId() {
		return userId;
	}
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public LocalDateTime getSlotTime() {
		return slotTime;
	}
	
	public void setSlotTime(LocalDateTime slotTime) {
		this.slotTime = slotTime;
	}
	
	public boolean isSent() {
		return sent;
	}
	
	public void setSent(boolean sent) {
		this.sent = sent;
	}
	
	public LocalDateTime getSentAt() {
		return sentAt;
	}
	
	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}
}
