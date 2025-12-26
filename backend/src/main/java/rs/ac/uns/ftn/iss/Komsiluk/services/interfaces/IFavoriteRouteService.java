package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.favoriteRoute.FavoriteRouteUpdateDTO;

public interface IFavoriteRouteService {

    public Collection<FavoriteRouteResponseDTO> getForUser(Long userId);

    public FavoriteRouteResponseDTO create(FavoriteRouteCreateDTO dto);

    public FavoriteRouteResponseDTO updateTitle(Long favoriteId, FavoriteRouteUpdateDTO dto);

    public void delete(Long favoriteId);
}
