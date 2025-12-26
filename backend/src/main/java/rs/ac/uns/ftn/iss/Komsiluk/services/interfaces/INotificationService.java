package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationResponseDTO;

public interface INotificationService {

    NotificationResponseDTO createNotification(NotificationCreateDTO dto);

    NotificationResponseDTO getNotification(Long id);

    Collection<NotificationResponseDTO> getUserNotifications(Long userId);

    Collection<NotificationResponseDTO> getUnreadUserNotifications(Long userId);

    NotificationResponseDTO markAsRead(Long id, boolean read);
}