package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Notification;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.AdminNotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationResponseDTO;

@Component
public class NotificationDTOMapper {
    private final ModelMapper modelMapper;

    @Autowired
    public NotificationDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public NotificationResponseDTO toResponseDTO(Notification notification) {
        NotificationResponseDTO dto = modelMapper.map(notification, NotificationResponseDTO.class);
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getId());
        }
        return dto;
    }

    // Za obične notifikacije
    public Notification fromCreateDto(NotificationCreateDTO dto, User user) {
        Notification n = modelMapper.map(dto, Notification.class);
        n.setId(null);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        n.setUser(user);
        return n;
    }

    // Za admin/sistemske notifikacije (zamenjuje AdminNotificationDTOMapper)
    public Notification fromAdminCreateDto(AdminNotificationCreateDTO dto) {
        Notification n = modelMapper.map(dto, Notification.class);
        n.setId(null);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        n.setUser(null);
        return n;
    }
}
