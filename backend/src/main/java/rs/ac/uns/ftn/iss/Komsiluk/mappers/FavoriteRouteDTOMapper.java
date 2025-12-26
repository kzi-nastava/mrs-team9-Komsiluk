package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.FavoriteRoute;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteResponseDTO;

@Component
public class FavoriteRouteDTOMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public FavoriteRouteDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public FavoriteRoute fromCreateDTO(FavoriteRouteCreateDTO dto, Route route, User owner, List<User> passengers) {
        FavoriteRoute fav = new FavoriteRoute();
        
        fav.setTitle(dto.getTitle());
        fav.setVehicleType(dto.getVehicleType());
        fav.setPetFriendly(dto.isPetFriendly());
        fav.setBabyFriendly(dto.isBabyFriendly());
        fav.setRoute(route);
        fav.setUser(owner);
        fav.setPassengers(passengers);
        return fav;
    }

    public FavoriteRouteResponseDTO toResponseDTO(FavoriteRoute fav) {
        FavoriteRouteResponseDTO dto = new FavoriteRouteResponseDTO();
        
        modelMapper.map(fav, dto);

        Route r = fav.getRoute();
        dto.setRouteId(r.getId());
        dto.setStartAddress(r.getStartAddress());
        dto.setEndAddress(r.getEndAddress());

        if (r.getStops() != null && !r.getStops().isEmpty()) {
            dto.setStops(Arrays.asList(r.getStops().split("\\|")));
        } else {
            dto.setStops(Collections.emptyList());
        }

        dto.setDistanceKm(r.getDistanceKm());
        dto.setEstimatedDurationMin(r.getEstimatedDurationMin());

        if (fav.getPassengers() != null) {
            List<Long> idList = fav.getPassengers().stream().map(User::getId).collect(Collectors.toList());
            dto.setPassengerIds(idList);
        } else {
            dto.setPassengerIds(Collections.emptyList());
        }

        return dto;
    }
}
