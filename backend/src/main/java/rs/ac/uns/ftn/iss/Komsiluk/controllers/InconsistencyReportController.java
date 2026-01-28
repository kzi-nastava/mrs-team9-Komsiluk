package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

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
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IInconsistencyReportService;

@RestController
@RequestMapping("/api/rides")
public class InconsistencyReportController {

    @Autowired
    private IInconsistencyReportService inconsistencyReportService;

    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    @GetMapping(value = "/{rideId}/inconsistencies", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<InconsistencyReportResponseDTO>> getByRide(@PathVariable Long rideId) {
        Collection<InconsistencyReportResponseDTO> reports = inconsistencyReportService.getByRideId(rideId);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    @PostMapping(value = "/{rideId}/inconsistencies",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InconsistencyReportResponseDTO> create(
            @PathVariable Long rideId,
            @RequestBody InconsistencyReportCreateDTO dto
    ) {
        InconsistencyReportResponseDTO created = inconsistencyReportService.create(rideId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }
}
