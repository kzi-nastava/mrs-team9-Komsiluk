package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import java.time.LocalDateTime;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;

@Component
public class DriverDTOMapper {

	private final ModelMapper modelMapper;
    private final VehicleDTOMapper vehicleMapper;

    @Autowired
    public DriverDTOMapper(ModelMapper modelMapper, VehicleDTOMapper vehicleMapper) {
        this.modelMapper = modelMapper;
        this.vehicleMapper = vehicleMapper;
    }

    // DriverCreateDTO → User (driver) + Vehicle
    public User fromCreateDTO(DriverCreateDTO dto) {
        User user = modelMapper.map(dto, User.class);

        user.setActive(false);
        user.setBlocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setRole(UserRole.DRIVER);
        user.setDriverStatus(DriverStatus.INACTIVE);

        Vehicle vehicle = vehicleMapper.fromCreateDTO(dto.getVehicle());
        user.setVehicle(vehicle);

        return user;
    }

    // User (driver) → DriverResponseDTO
    public DriverResponseDTO toResponseDTO(User user) {
        DriverResponseDTO dto = modelMapper.map(user, DriverResponseDTO.class);
        dto.setVehicle(vehicleMapper.toResponseDTO(user.getVehicle()));

        return dto;
    }
}
