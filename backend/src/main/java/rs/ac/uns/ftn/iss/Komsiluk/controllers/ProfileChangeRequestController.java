package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IProfileChangeRequestService;

@RestController
@RequestMapping("/api/driver-edit-requests")
public class ProfileChangeRequestController {

    private final IProfileChangeRequestService service;

    @Autowired
    public ProfileChangeRequestController(IProfileChangeRequestService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('DRIVER')")
    @PostMapping("/{driverId}")
    public ResponseEntity<ProfileChangeRequestResponseDTO> create(@PathVariable Long driverId, @Valid @RequestBody ProfileChangeRequestCreateDTO dto) {
        var created = service.createRequest(driverId, dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<Collection<ProfileChangeRequestResponseDTO>> getPending() {
        return ResponseEntity.ok(service.getPendingRequests());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{requestId}/approve/{adminId}")
    public ResponseEntity<ProfileChangeRequestResponseDTO> approve(@PathVariable Long requestId, @PathVariable Long adminId) {
        var updated = service.approve(requestId, adminId);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{requestId}/reject/{adminId}")
    public ResponseEntity<ProfileChangeRequestResponseDTO> reject(@PathVariable Long requestId, @PathVariable Long adminId) {
        var updated = service.reject(requestId, adminId);
        return ResponseEntity.ok(updated);
    }
}
