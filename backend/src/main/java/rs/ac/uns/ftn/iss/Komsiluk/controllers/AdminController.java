package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.AdminRideSortBy;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.AdminRideDetailsDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.AdminRideHistoryDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRideService;
import rs.ac.uns.ftn.iss.Komsiluk.util.validators.ValidDateRange;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    @Autowired
    private IRideService rideService;

//    @GetMapping(value = "/rides", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Collection<AdminRideHistoryDTO>> getAllRides(
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate from,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate to,
//
//            @RequestParam(required = false)
//            String sortBy
//    ) {
//        return ResponseEntity.ok(
//                rideService.getAdminRideHistory(from, to, sortBy)
//        );
//    }

    @GetMapping(value = "/users/{userId}/rides", produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidDateRange
    public ResponseEntity<Collection<AdminRideHistoryDTO>> getUserRides(
            @PathVariable @Positive Long userId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(required = false)
            AdminRideSortBy sortBy
    ) {
        return ResponseEntity.ok(
                rideService.getAdminRideHistoryForUser(userId, from, to, sortBy)
        );
    }



    @GetMapping(value = "/rides/{rideId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdminRideDetailsDTO> getRideDetails(
            @PathVariable @Positive(message = "Ride ID must be positive") Long rideId
    ) {
        return ResponseEntity.ok(
                rideService.getAdminRideDetails(rideId)
        );
    }

}
