package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRouteService;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

	@Autowired
    private IRouteService routeService;

	@PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }

	@PreAuthorize("hasAnyRole('PASSENGER', 'DRIVER')")
    @PostMapping(value = "/find-or-create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RouteResponseDTO> findOrCreate(@Valid @RequestBody RouteCreateDTO dto) {
        RouteResponseDTO route = routeService.findOrCreate(dto);
        return ResponseEntity.ok(route);
    }
}