package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleUpdateDTO;

@Component
public class VehicleDTOMapper {
	
	private final ModelMapper modelMapper;

    @Autowired
    public VehicleDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // VehicleCreateDTO → Vehicle
    public Vehicle fromCreateDTO(VehicleCreateDTO dto) {
        if (dto == null) return null;
		return modelMapper.map(dto, Vehicle.class);
    }
    
    //VehicleUpdateDTO → Vehicle
    public void fromUpdateDTO(VehicleUpdateDTO dto, Vehicle vehicle) {
		if (dto == null || vehicle == null) return;
		modelMapper.map(dto, vehicle);
	}

    // Vehicle → VehicleResponseDTO
    public VehicleResponseDTO toResponseDTO(Vehicle vehicle) {
        if (vehicle == null) return null;
        return modelMapper.map(vehicle, VehicleResponseDTO.class);
    }
}
