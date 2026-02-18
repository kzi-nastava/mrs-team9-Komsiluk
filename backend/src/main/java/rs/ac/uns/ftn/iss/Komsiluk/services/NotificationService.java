package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Notification;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.AdminNotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.NotificationDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.NotificationRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.INotificationService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;
import rs.ac.uns.ftn.iss.Komsiluk.socket.services.NotificationSocketPublisher;

@Service
public class NotificationService implements INotificationService {

	@Autowired
    private NotificationRepository repository;
	@Autowired
    private NotificationDTOMapper mapper;
	@Autowired
	private IUserService userService;
	@Autowired
	private NotificationSocketPublisher socketPublisher;
	@Autowired
	private PushNotificationService pushNotificationService;

    @Override
    public NotificationResponseDTO createNotification(NotificationCreateDTO dto) {
        User user = userService.findById(dto.getUserId());

        Notification notification = mapper.fromCreateDto(dto, user);
        Notification saved = repository.save(notification);
        
        NotificationResponseDTO response = mapper.toResponseDTO(saved);
        
        socketPublisher.sendToUser(user.getEmail(), response);
        pushNotificationService.sendToUser(user.getId(), response.getTitle() , response.getMessage(), Map.of("type", response.getType().name()));

        return response;
    }

    @Override
    public NotificationResponseDTO getNotification(Long id) {
        Notification n = repository.findById(id).orElseThrow(NotFoundException::new);
        return mapper.toResponseDTO(n);
    }

    @Override
    public Collection<NotificationResponseDTO> getUserNotifications(Long userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public Collection<NotificationResponseDTO> getUnreadUserNotifications(Long userId) {
        return repository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDTO markAsRead(Long id, boolean read) {
        Notification n = repository.findById(id).orElseThrow(NotFoundException::new);
        n.setRead(read);
        Notification saved = repository.save(n);
        return mapper.toResponseDTO(saved);
    }

    public Collection<NotificationResponseDTO> getUnreadPanics() {
        List<Notification> panics = repository.findAllUnreadPanics();
        return panics.stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public NotificationResponseDTO createAdminNotification(AdminNotificationCreateDTO dto) {
        Notification notification = mapper.fromAdminCreateDto(dto);
        Notification saved = repository.save(notification);
        NotificationResponseDTO response = mapper.toResponseDTO(saved);

        socketPublisher.sendToAdmins(response);

        pushNotificationService.sendToAllAdmins(
                response.getTitle(),
                response.getMessage(),
                Map.of("type", "PANIC")
        );

        return response;
    }
}