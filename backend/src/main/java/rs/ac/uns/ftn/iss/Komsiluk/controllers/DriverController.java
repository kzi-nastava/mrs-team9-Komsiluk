package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverStatusUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverService;

@RestController
@RequestMapping(value = "/api/drivers")
public class DriverController {

	@Autowired
    private IDriverService driverService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverResponseDTO>> getAllDrivers() {
        Collection<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverResponseDTO> getDriver(@PathVariable("id") Long id) {
        DriverResponseDTO driver = driverService.getDriver(id);
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverResponseDTO> registerDriver(@RequestBody DriverCreateDTO dto) {
        DriverResponseDTO created = driverService.registerDriver(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<DriverResponseDTO> updateStatus(@PathVariable Long id, @RequestBody DriverStatusUpdateDTO dto) {
        DriverResponseDTO updated = driverService.updateDriverStatus(id, dto.getStatus());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
