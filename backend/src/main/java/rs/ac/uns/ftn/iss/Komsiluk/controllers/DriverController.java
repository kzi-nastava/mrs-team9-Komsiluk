package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;
import java.time.LocalDate;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverBasicDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverStatusUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverService;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRideService;

@RestController
@RequestMapping(value = "/api/drivers")
public class DriverController {

	@Autowired
    private IDriverService driverService;
    @Autowired
    private IRideService rideService;


    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverResponseDTO>> getAllDrivers() {
        Collection<DriverResponseDTO> drivers = driverService.getAllDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverResponseDTO> getDriver(@PathVariable("id") Long id) {
        DriverResponseDTO driver = driverService.getDriver(id);
        return new ResponseEntity<>(driver, HttpStatus.OK);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverResponseDTO> registerDriver(@Valid @RequestPart("data") DriverCreateDTO dto, @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        DriverResponseDTO created = driverService.registerDriver(dto, profileImage);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PutMapping("/{id}/status")
    public ResponseEntity<DriverResponseDTO> updateStatus(@PathVariable Long id, @RequestBody DriverStatusUpdateDTO dto) {
        DriverResponseDTO updated = driverService.updateDriverStatus(id, dto.getStatus());
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
    @PreAuthorize("hasRole('DRIVER')")
    @GetMapping(value = "/{id}/rides/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<RideResponseDTO>> getDriverRideHistory(
            @PathVariable("id") Long driverId,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate to
    ) {
        Collection<RideResponseDTO> history = rideService.getDriverRideHistory(driverId, from, to);
        return new ResponseEntity<>(history, HttpStatus.OK);
    }
    @GetMapping(value = "/basic", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverBasicDTO>> getDriversBasic() {
        return new ResponseEntity<>(driverService.getDriversBasic(), HttpStatus.OK);
    }

}
