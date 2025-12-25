package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.DriverDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.AlreadyExistsException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IVehicleService;

@Service
public class DriverService implements IDriverService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private DriverDTOMapper driverMapper;
	@Autowired
    private IUserTokenService userTokenService;
	@Autowired
	private IVehicleService vehicleService;

    @Override
    public Collection<DriverResponseDTO> getAllDrivers() {
        return userRepository.findAll().stream().filter(u -> u.getRole() == UserRole.DRIVER).map(driverMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public DriverResponseDTO getDriver(Long id) {
        User user = userRepository.findById(id);
        if (user == null || user.getRole() != UserRole.DRIVER) {
            throw new NotFoundException();
        }
        return driverMapper.toResponseDTO(user);
    }

    @Override
    public DriverResponseDTO registerDriver(DriverCreateDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException();
        }

        User driver = driverMapper.fromCreateDTO(dto);

        driver.setRole(UserRole.DRIVER);
        driver.setActive(false);
        driver.setDriverStatus(DriverStatus.INACTIVE);
        
        if (dto.getVehicle() != null) {
			driver.getVehicle().setId(vehicleService.create(dto.getVehicle()).getId());
		}

        userRepository.save(driver);

        userTokenService.createActivationToken(driver.getId());

        return driverMapper.toResponseDTO(driver);
    }
}
