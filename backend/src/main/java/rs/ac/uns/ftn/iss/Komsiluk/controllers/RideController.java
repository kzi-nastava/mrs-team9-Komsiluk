package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
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

	@PreAuthorize("hasRole('PASSENGER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RideResponseDTO> orderRide(@Valid @RequestBody RideCreateDTO dto) {
        RideResponseDTO created = rideService.orderRide(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
	
	@PreAuthorize("hasRole('DRIVER')")
	@GetMapping("/driver/{driverId}/current")
	public ResponseEntity<RideResponseDTO> getDriverCurrentRide(@PathVariable Long driverId) {
	    RideResponseDTO dto = rideService.getCurrentRideForDriver(driverId);

	    if (dto == null) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    @GetMapping("/user/{userId}/scheduled")
    public ResponseEntity<Collection<RideResponseDTO>> getScheduledRidesForUser(@PathVariable Long userId) {
        Collection<RideResponseDTO> rides = rideService.getScheduledRidesForUser(userId);
        return new ResponseEntity<>(rides, HttpStatus.OK);
    }
    
	@PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{id}/start")
    public ResponseEntity<RideResponseDTO> startRide(@PathVariable Long id) {
        RideResponseDTO dto = rideService.startRide(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

	@PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{id}/cancel/driver")
    public ResponseEntity<Void> cancelByDriver(
            @PathVariable @Positive Long id,
            @Valid @RequestBody DriverCancelRideDTO dto) {

        rideService.cancelByDriver(id, dto);
        return ResponseEntity.ok().build();
    }

	@PreAuthorize("hasRole('PASSENGER')")
    @PostMapping("/{id}/cancel/passenger")
    public ResponseEntity<Void> cancelByPassenger(
            @PathVariable @Positive Long id,
            @Valid @RequestBody PassengerCancelRideDTO dto) {

        rideService.cancelByPassenger(id, dto);
        return ResponseEntity.ok().build();
    }

	@PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{id}/stop")
    public ResponseEntity<RideResponseDTO> stopRide(
            @PathVariable @Positive Long id,
            @Valid @RequestBody StopRideRequestDTO dto) {

        RideResponseDTO responseDTO = rideService.stopRide(id, dto);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

	@PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    @PostMapping("/{id}/panic")
    public ResponseEntity<Void> invokePanicButton(
            @PathVariable @Positive Long id,
            @Valid @RequestBody PanicRequestDTO dto) {

        rideService.handlePanicButton(id, dto);
        return ResponseEntity.ok().build();
    }

	@PreAuthorize("hasRole('DRIVER')")
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

    @PreAuthorize("hasRole('PASSENGER')")
    @GetMapping("/passenger/active")
    public ResponseEntity<RidePassengerActiveDTO> getActiveRide() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return rideService.getActiveRideForPassenger(currentUser.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping(value = "/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'PASSENGER')")
    public ResponseEntity<AdminRideDetailsDTO> getRideDetails(
            @PathVariable @Positive(message = "Ride ID must be positive") Long rideId
    ) {
        return ResponseEntity.ok(
                rideService.getAdminRideDetails(rideId)
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active/all")
    public ResponseEntity<Collection<RideResponseDTO>> getAllActiveRides() {
        Collection<RideResponseDTO> activeRides = rideService.getAllActiveRides();
        return new ResponseEntity<>(activeRides, HttpStatus.OK);
    }

}

