package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IVehicleService;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.map.ActiveVehicleOnMapDTO;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {
	
	@Autowired
	private IVehicleService vehicleService;
	
    @GetMapping(value = "/active-map", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<ActiveVehicleOnMapDTO>> getActiveVehiclesOnMap() {
        Collection<ActiveVehicleOnMapDTO> vehicles = vehicleService.getActiveVehiclesOnMap();
        return new ResponseEntity<Collection<ActiveVehicleOnMapDTO>>(vehicles, HttpStatus.OK);
    }
}