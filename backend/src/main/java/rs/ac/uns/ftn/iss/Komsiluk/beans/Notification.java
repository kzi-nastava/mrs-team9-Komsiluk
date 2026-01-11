package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;

@Entity
@Table(name = "notifications")
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
    private NotificationType type;
	
	@Column(nullable = false)
    private String title;
	
	@Column(nullable = false)
    private String message;
	
	@Column(nullable = false)
    private LocalDateTime createdAt;
	
	@Column(nullable = false)
    private boolean read;
	
	@Column(columnDefinition = "TEXT")
    private String metadata;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public Notification() {
		super();
    }

	public Notification(Long id, NotificationType type, String title, String message, LocalDateTime createdAt,
			boolean read, String metadata, User user) {
		super();
		this.id = id;
		this.type = type;
		this.title = title;
		this.message = message;
		this.createdAt = createdAt;
		this.read = read;
		this.metadata = metadata;
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
