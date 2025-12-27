package rs.ac.uns.ftn.iss.Komsiluk.beans;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;

public class Notification {
	
	private Long id;
    private NotificationType type;
    private String title;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;
    private String metadata;
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
