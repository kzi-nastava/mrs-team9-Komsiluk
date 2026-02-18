package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Notification;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.AdminNotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationResponseDTO;

@Component
public class AdminNotificationDTOMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public AdminNotificationDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public Notification fromCreateDto(AdminNotificationCreateDTO dto) {
        Notification n = modelMapper.map(dto, Notification.class);
        n.setId(null);
        n.setCreatedAt(LocalDateTime.now());
        n.setRead(false);
        n.setUser(null);
        return n;
    }

    public NotificationResponseDTO toResponseDTO(Notification notification) {
        NotificationResponseDTO dto = modelMapper.map(notification, NotificationResponseDTO.class);
        dto.setUserId(null);
        return dto;
    }
}