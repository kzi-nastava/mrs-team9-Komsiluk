package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IFavoriteRouteService;

@RestController
@PreAuthorize("hasRole('PASSENGER')")
@RequestMapping("/api")
public class FavoriteRouteController {

	@Autowired
    private IFavoriteRouteService favoriteRouteService;

    @GetMapping("/users/{userId}/favorites")
    public ResponseEntity<Collection<FavoriteRouteResponseDTO>> getFavorites(@PathVariable Long userId) {
        Collection<FavoriteRouteResponseDTO> result = favoriteRouteService.getForUser(userId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/users/{userId}/favorites")
    public ResponseEntity<FavoriteRouteResponseDTO> addFavorite(@PathVariable Long userId, @Valid @RequestBody FavoriteRouteCreateDTO dto) {
        dto.setUserId(userId);
        FavoriteRouteResponseDTO created = favoriteRouteService.create(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/favorites/{favoriteId}")
    public ResponseEntity<FavoriteRouteResponseDTO> renameFavorite(@PathVariable Long favoriteId, @Valid @RequestBody FavoriteRouteUpdateDTO dto) {
        FavoriteRouteResponseDTO updated = favoriteRouteService.updateTitle(favoriteId, dto);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/favorites/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId) {
        favoriteRouteService.delete(favoriteId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
