package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.AdminRideSortBy;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.AdminRideHistoryDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRideService;
import rs.ac.uns.ftn.iss.Komsiluk.util.validators.ValidDateRange;

import java.time.LocalDate;
import java.util.Collection;

@RestController
@RequestMapping("/api/admin")
@Validated
public class AdminController {

    @Autowired
    private IRideService rideService;

    @GetMapping(value = "/rides/by-user-email", produces = MediaType.APPLICATION_JSON_VALUE)
    @ValidDateRange
    public ResponseEntity<Collection<AdminRideHistoryDTO>> getRidesByUserEmail(
            @RequestParam String email,

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
                rideService.getAdminRideHistoryByEmail(email, from, to, sortBy)
        );
    }

}
