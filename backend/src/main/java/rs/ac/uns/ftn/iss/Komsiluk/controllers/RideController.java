package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.*;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideLiveInfoDTO;
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

    @PostMapping("/{id}/cancel/driver")
    public ResponseEntity<Void> cancelByDriver(
            @PathVariable Long id,
            @RequestBody DriverCancelRideDTO dto) {

        rideService.cancelByDriver(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/cancel/passenger")
    public ResponseEntity<Void> cancelByPassenger(
            @PathVariable Long id,
            @RequestBody PassengerCancelRideDTO dto) {

        rideService.cancelByPassenger(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<Void> stopRide(
            @PathVariable Long id,
            @RequestBody StopRideRequestDTO dto) {

        rideService.stopRide(id, dto);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/{id}/finish")
    public ResponseEntity<RideResponseDTO> finishRide(@PathVariable Long id) {
        RideResponseDTO dto = rideService.finishRide(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/live", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideLiveInfoDTO> getLiveInfo(@PathVariable Long id) {
        RideLiveInfoDTO dto = rideService.getLiveInfo(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}

