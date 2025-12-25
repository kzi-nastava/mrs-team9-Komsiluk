package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.VehicleDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.VehicleRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.AlreadyExistsException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IVehicleService;

@Service
public class VehicleService implements IVehicleService {
	
	@Autowired
	private VehicleRepository vehicleRepository;
	@Autowired
    private VehicleDTOMapper vehicleMapper;


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
    	if (vehicleRepository.existsByLicencePlate(dto.getLicencePlate())) {
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
			if (vehicleRepository.existsByLicencePlate(dto.getLicencePlate())) {
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
}
