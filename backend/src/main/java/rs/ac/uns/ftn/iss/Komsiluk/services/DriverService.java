package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverBasicDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.DriverDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.VehicleDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.AlreadyExistsException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverActivityService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;

@Service
public class DriverService implements IDriverService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
    private DriverDTOMapper driverMapper;
	@Autowired
    private IUserTokenService userTokenService;
	@Autowired
	private IDriverActivityService driverActivityService;
    @Autowired
    private MailService mailService;
    @Autowired
    private VehicleDTOMapper vehicleMapper;
    @Autowired
    private IUserService userService;
    @Value("${app.user.default-profile-image}")
    private String defaultProfileImageUrl;

    @Override
    public Collection<DriverResponseDTO> getAllDrivers() {
        return userRepository.findAll().stream().filter(u -> u.getRole() == UserRole.DRIVER).map(driverMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public DriverResponseDTO getDriver(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Driver not found"));
        if (user.getRole() != UserRole.DRIVER) {
            throw new NotFoundException("Driver not found");
        }
        return driverMapper.toResponseDTO(user);
    }

    @Override
    @Transactional
    public DriverResponseDTO registerDriver(DriverCreateDTO dto, MultipartFile profileImage) {

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            throw new AlreadyExistsException("User with that email already exists");
        }

        User driver = driverMapper.fromCreateDTO(dto);

        driver.setRole(UserRole.DRIVER);
        driver.setActive(false);
        driver.setDriverStatus(DriverStatus.INACTIVE);

        driver.setProfileImageUrl(defaultProfileImageUrl);

        if (dto.getVehicle() != null) {
            Vehicle vehicle = vehicleMapper.fromCreateDTO(dto.getVehicle());
            driver.setVehicle(vehicle);
        }

        userRepository.save(driver);

        if (profileImage != null && !profileImage.isEmpty()) {
            userService.updateProfileImage(driver.getId(), profileImage);
        }

        UserTokenResponseDTO token = userTokenService.createActivationToken(driver.getId());

        mailService.sendDriverActivationMail(driver.getEmail(), token.getToken());

        return driverMapper.toResponseDTO(driver);
    }

    @Override
    public DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus) {
        User driver = userRepository.findById(driverId).orElseThrow(() -> new NotFoundException("Driver not found"));

        DriverStatus oldStatus= driver.getDriverStatus();
        
        if (oldStatus == DriverStatus.INACTIVE && newStatus != DriverStatus.INACTIVE) {
            driverActivityService.startActivity(driver);
        }

        if (oldStatus != DriverStatus.INACTIVE && newStatus == DriverStatus.INACTIVE) {
            driverActivityService.endActivity(driver);
        }
            
        driver.setDriverStatus(newStatus);

        userRepository.save(driver);

        return driverMapper.toResponseDTO(driver);
    }

    @Override
    public Collection<DriverBasicDTO> getDriversBasic() {
        return userRepository.findDriverBasics();
    }

}
