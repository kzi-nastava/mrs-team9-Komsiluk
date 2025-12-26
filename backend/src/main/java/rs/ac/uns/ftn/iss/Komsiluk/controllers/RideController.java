package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRideService;

@RestController
@RequestMapping("/api/rides")
public class RideController {

	@Autowired
    private IRideService rideService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideResponseDTO> orderRide(@RequestBody RideCreateDTO dto) {
        RideResponseDTO created = rideService.orderRide(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
