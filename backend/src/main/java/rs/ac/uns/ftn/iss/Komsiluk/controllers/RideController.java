package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideEstimateRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideEstimateResponseDTO;
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
    
    @PostMapping("/{id}/start")
    public ResponseEntity<RideResponseDTO> startRide(@PathVariable Long id) {
        RideResponseDTO dto = rideService.startRide(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping("/estimate")
    public ResponseEntity<RideEstimateResponseDTO> estimateRide(
            @RequestBody RideEstimateRequestDTO dto) {

        RideEstimateResponseDTO response = rideService.estimate(dto);

        return ResponseEntity.ok(response);
    }

}
