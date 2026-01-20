package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IInconsistencyReportService;

@RestController
@RequestMapping("/api/rides")
public class InconsistencyReportController {

    @Autowired
    private IInconsistencyReportService inconsistencyReportService;

    @GetMapping(value = "/{rideId}/inconsistencies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<InconsistencyReportResponseDTO>> getByRide(@PathVariable Long rideId) {
        Collection<InconsistencyReportResponseDTO> reports = inconsistencyReportService.getByRideId(rideId);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @PostMapping(value = "/{rideId}/inconsistencies", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InconsistencyReportResponseDTO> create(
            @PathVariable Long rideId,
            @RequestBody InconsistencyReportCreateDTO dto
    ) {
        // Dobijanje trenutnog ulogovanog korisnika iz tokena
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long loggedInUserId = Long.parseLong(auth.getName()); // username u tokenu je userId

       System.out.println(loggedInUserId);
       System.out.println(rideId);


        // Prosledi dto dalje u servis
        InconsistencyReportResponseDTO created = inconsistencyReportService.create(rideId, dto);
        System.out.println(created.getMessage());

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
