package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;

public interface IDriverService {

    public Collection<DriverResponseDTO> getAllDrivers();

    public DriverResponseDTO getDriver(Long id);

    public DriverResponseDTO registerDriver(DriverCreateDTO dto);

    public DriverResponseDTO updateDriverStatus(Long driverId, DriverStatus newStatus);
}
