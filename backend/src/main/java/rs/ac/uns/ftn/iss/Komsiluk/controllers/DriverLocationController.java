package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.location.DriverLocationUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverLocationService;

@RestController
@RequestMapping("/api/drivers")
public class DriverLocationController {

    @Autowired
    private IDriverLocationService driverLocationService;

    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateLocation(@PathVariable Long id, @RequestBody DriverLocationUpdateDTO dto) {
        driverLocationService.updateLiveLocation(id, dto.getLat(), dto.getLng());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
