package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.map.ActiveVehicleOnMapDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.VehicleDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.VehicleRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.AlreadyExistsException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverLocationService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IVehicleService;


@Service
public class VehicleService implements IVehicleService {
	
	@Autowired
	private VehicleRepository vehicleRepository;
	@Autowired
    private VehicleDTOMapper vehicleMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IDriverLocationService driverLocationService;

	@Override
    public Collection<VehicleResponseDTO> getAll() {
    	return vehicleRepository.findAll().stream().map(vehicleMapper::toResponseDTO).collect(Collectors.toList());
    }
    
	@Override
    public VehicleResponseDTO getOne(Long id) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow(NotFoundException::new);
        return vehicleMapper.toResponseDTO(v);
    }

	@Override
    public VehicleResponseDTO create(VehicleCreateDTO dto) {
    	if (vehicleRepository.existsByLicencePlateIgnoreCase(dto.getLicencePlate())) {
    		throw new AlreadyExistsException();
    	}
    	Vehicle vehicle = vehicleMapper.fromCreateDTO(dto);
    	vehicleRepository.save(vehicle);
    	return vehicleMapper.toResponseDTO(vehicle);
    }

	@Override
    public VehicleResponseDTO update(Long id, VehicleUpdateDTO dto) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow(NotFoundException::new);
        if (dto.getLicencePlate() != null && !dto.getLicencePlate().equals(v.getLicencePlate())) {
			if (vehicleRepository.existsByLicencePlateIgnoreCase(dto.getLicencePlate())) {
				throw new AlreadyExistsException();
			}
		}
        vehicleMapper.fromUpdateDTO(dto, v);
        vehicleRepository.save(v);
        return vehicleMapper.toResponseDTO(v);
    }

	@Override
    public void delete(Long id) {
        vehicleRepository.findById(id).orElseThrow(NotFoundException::new);
		vehicleRepository.deleteById(id);
    }
	
	@Override
	public Vehicle save(Vehicle vehicle) {
		return vehicleRepository.save(vehicle);
	}
    @Override
    public Collection<ActiveVehicleOnMapDTO> getActiveVehiclesOnMap() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.DRIVER)
                .filter(u -> u.getDriverStatus() != DriverStatus.INACTIVE)
                .map(this::toActiveVehicleOnMapDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private ActiveVehicleOnMapDTO toActiveVehicleOnMapDTO(User driver) {
        if (driver.getVehicle() == null) {
            return null;
        }

        DriverLocation loc = driverLocationService.getLiveLocation(driver.getId());
        if (loc == null) {
            return null;
        }

        ActiveVehicleOnMapDTO dto = new ActiveVehicleOnMapDTO();
        dto.setDriverId(driver.getId());
        dto.setBusy(driver.getDriverStatus() == DriverStatus.IN_RIDE);
        dto.setLatitude(loc.getLat());
        dto.setLongitude(loc.getLng());
        dto.setVehicle(vehicleMapper.toResponseDTO(driver.getVehicle()));

        return dto;
    }
}
