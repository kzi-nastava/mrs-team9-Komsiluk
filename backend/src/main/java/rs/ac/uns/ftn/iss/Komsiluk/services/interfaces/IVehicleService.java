package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleUpdateDTO;

public interface IVehicleService {
	
	public Collection<VehicleResponseDTO> getAll();
	
	public VehicleResponseDTO getOne(Long vehicleId);
	
	public VehicleResponseDTO create(VehicleCreateDTO vehicle);
	
	public VehicleResponseDTO update(Long vehicleId, VehicleUpdateDTO vehicle);
	
	public void delete(Long vehicleId);
	
}
