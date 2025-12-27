package rs.ac.uns.ftn.iss.Komsiluk.dtos.notification;

import java.time.LocalDateTime;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;

public class NotificationResponseDTO {
	
    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private String metadata;
    private boolean read;
    private LocalDateTime createdAt;
    
    public NotificationResponseDTO() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
