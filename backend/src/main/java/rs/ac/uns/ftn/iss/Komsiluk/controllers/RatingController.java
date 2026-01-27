package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRatingService;

@RestController
@RequestMapping("/api/rides")
public class RatingController {

    @Autowired
    private IRatingService ratingService;

    @PreAuthorize("hasRole('PASSENGER')")
    @PostMapping(
            value = "/{rideId}/ratings",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RatingResponseDTO> create(
            @PathVariable Long rideId,
            @RequestBody RatingCreateDTO dto
    ) {
        RatingResponseDTO created = ratingService.createRating(rideId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
    @PreAuthorize("hasAnyRole('DRIVER', 'PASSENGER')")
    @GetMapping(
            value = "/{rideId}/ratings",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<RatingResponseDTO>> getAllForRide(@PathVariable Long rideId) {
        List<RatingResponseDTO> ratings = ratingService.getRatingsForRide(rideId);
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @GetMapping(
            value = "/{rideId}/ratings/{raterId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<RatingResponseDTO> getOneForRideByRater(
            @PathVariable Long rideId,
            @PathVariable Long raterId
    ) {
        RatingResponseDTO rating = ratingService.getRatingForRideByRater(rideId, raterId);
        return new ResponseEntity<>(rating, HttpStatus.OK);
    }
}
