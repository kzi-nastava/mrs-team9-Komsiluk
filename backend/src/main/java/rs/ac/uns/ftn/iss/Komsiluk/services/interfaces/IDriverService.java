package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import org.springframework.web.multipart.MultipartFile;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverBasicDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;

public interface IDriverService {

    public Collection<DriverResponseDTO> getAllDrivers();

    public DriverResponseDTO getDriver(Long id);

    public DriverResponseDTO registerDriver(DriverCreateDTO dto, MultipartFile profileImage);

    public DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus);

    Collection<DriverBasicDTO> getDriversBasic();
}
