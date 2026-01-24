package rs.ac.uns.ftn.iss.Komsiluk.dtos.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;

public class NotificationCreateDTO {

	@NotNull
	@Positive
    private Long userId;
	
	@NotNull
    private NotificationType type;
	
	@NotBlank
	@Size(min = 2, max = 50)
    private String title;
	
	@NotBlank
	@Size(min = 2, max = 500)
    private String message;
	
	@Size(max = 1000)
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
