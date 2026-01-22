package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.location.DriverLocationResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.location.DriverLocationUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverLocationService;

import java.util.Collection;

@RestController
@RequestMapping("/api/drivers")
public class DriverLocationController {

    @Autowired
    private IDriverLocationService driverLocationService;

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @RequestBody DriverLocationUpdateDTO dto) {
        driverLocationService.updateLiveLocation(id, dto.getLat(), dto.getLng());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    // GET: sve aktivne lokacije (anonimno dozvoljeno u SecurityConfig)
    @GetMapping(value = "/locations", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<DriverLocationResponseDTO>> getAllActiveDriverLocations() {
        Collection<DriverLocation> locations = driverLocationService.getAllLiveLocations();

        Collection<DriverLocationResponseDTO> dtos = locations.stream()
                .map(this::toDtoWithBusy)
                .toList();

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/location", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DriverLocationResponseDTO> getDriverLocation(@PathVariable Long id) {
        DriverLocation loc = driverLocationService.getLiveLocation(id);
        if (loc == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(toDtoWithBusy(loc), HttpStatus.OK);
    }

    private DriverLocationResponseDTO toDtoWithBusy(DriverLocation loc) {
        DriverLocationResponseDTO dto = new DriverLocationResponseDTO();
        dto.setDriverId(loc.getDriverId());
        dto.setLat(loc.getLat());
        dto.setLng(loc.getLng());
        dto.setUpdatedAt(loc.getUpdatedAt());

        var driver = userRepository.findById(loc.getDriverId()).orElse(null);
        boolean busy = driver != null && driver.getDriverStatus() == DriverStatus.IN_RIDE;
        dto.setBusy(busy);

        return dto;
    }

}
