package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRouteService;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

	@Autowired
    private IRouteService routeService;

	// debug
    @GetMapping
    public ResponseEntity<Collection<RouteResponseDTO>> getAll() {
        return ResponseEntity.ok(routeService.getAll());
    }

    // debug
    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getById(id));
    }

    // debug
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RouteResponseDTO> create(@RequestBody RouteCreateDTO dto) {
        RouteResponseDTO created = routeService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/find-or-create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RouteResponseDTO> findOrCreate(@RequestBody RouteCreateDTO dto) {
        RouteResponseDTO route = routeService.findOrCreate(dto);
        return ResponseEntity.ok(route);
    }
}