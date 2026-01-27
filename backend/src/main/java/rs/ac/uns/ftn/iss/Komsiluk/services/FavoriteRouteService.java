package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.FavoriteRoute;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.FavoriteRouteDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.FavoriteRouteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RouteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IFavoriteRouteService;

@Service
public class FavoriteRouteService implements IFavoriteRouteService {

	@Autowired
    private FavoriteRouteRepository favoriteRouteRepository;
	@Autowired
    private RouteRepository routeRepository;
	@Autowired
    private UserRepository userRepository;
	@Autowired
    private FavoriteRouteDTOMapper mapper;

    @Override
    public Collection<FavoriteRouteResponseDTO> getForUser(Long userId) {
        return favoriteRouteRepository.findAllByUserId(userId).stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public FavoriteRouteResponseDTO create(FavoriteRouteCreateDTO dto) {
        Route route = routeRepository.findById(dto.getRouteId()).orElse(null);
        if (route == null) throw new NotFoundException("Route not found");

        User owner = userRepository.findById(dto.getUserId()).orElseThrow(() -> new NotFoundException("User not found"));

        List<User> passengers = new ArrayList<>();
        if (dto.getPassengersEmails() != null) {
            for (String email : dto.getPassengersEmails()) {
                User u = userRepository.findByEmailIgnoreCase(email);
                if (u == null) {
                    throw new NotFoundException("Passenger with email " + email + " not found");
                }
                passengers.add(u);
            }
        }

        FavoriteRoute fav = mapper.fromCreateDTO(dto, route, owner, passengers);
        favoriteRouteRepository.save(fav);

        return mapper.toResponseDTO(fav);
    }

    @Override
    public FavoriteRouteResponseDTO updateTitle(Long favoriteId, FavoriteRouteUpdateDTO dto) {
        FavoriteRoute fav = favoriteRouteRepository.findById(favoriteId).orElseThrow(() -> new NotFoundException("Favorite route not found"));
        if (fav == null) throw new NotFoundException("Favorite route not found");

        fav.setTitle(dto.getTitle());
        favoriteRouteRepository.save(fav);

        return mapper.toResponseDTO(fav);
    }

    @Override
    public void delete(Long favoriteId) {
        FavoriteRoute fav = favoriteRouteRepository.findById(favoriteId).orElseThrow(() -> new NotFoundException("Favorite route not found"));
        if (fav == null) throw new NotFoundException("Favorite route not found");
        favoriteRouteRepository.deleteById(favoriteId);
    }
}
