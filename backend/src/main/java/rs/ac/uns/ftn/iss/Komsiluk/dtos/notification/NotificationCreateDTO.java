package rs.ac.uns.ftn.iss.Komsiluk.dtos.notification;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;

public class NotificationCreateDTO {

    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private String metadata;
    
    public NotificationCreateDTO() {
		super();
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
}
