package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.vehicle.VehicleUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IVehicleService;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
	
	@Autowired
	private IVehicleService vehicleService;

	// debug
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<VehicleResponseDTO>> getAll()
	{
		Collection<VehicleResponseDTO> vehicles = vehicleService.getAll();
		return new ResponseEntity<Collection<VehicleResponseDTO>>(vehicles, HttpStatus.OK);
	}
	
	@GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VehicleResponseDTO> getOne(@PathVariable("id") Long id) throws Exception
	{
		VehicleResponseDTO vehicle = vehicleService.getOne(id);
		return new ResponseEntity<VehicleResponseDTO>(vehicle, HttpStatus.OK);
	}

	// debug
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VehicleResponseDTO> create(@RequestBody VehicleCreateDTO vehicle) throws Exception{
		VehicleResponseDTO createdVehicle = vehicleService.create(vehicle);
		return new ResponseEntity<VehicleResponseDTO>(createdVehicle, HttpStatus.CREATED);
	}
	
	// debug
	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<VehicleResponseDTO> update(@RequestBody VehicleUpdateDTO vehicle, @PathVariable Long id) throws Exception{
		VehicleResponseDTO updatedVehicle = vehicleService.update(id,vehicle);
		return new ResponseEntity<VehicleResponseDTO>(updatedVehicle, HttpStatus.OK);
	}
	
	@DeleteMapping(value="/{id}")
	public ResponseEntity<VehicleResponseDTO> delete(@PathVariable Long id) throws Exception{
		vehicleService.delete(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}