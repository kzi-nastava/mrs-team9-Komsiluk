package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;

public interface IRouteService {

    public Collection<RouteResponseDTO> getAll();

    public RouteResponseDTO getById(Long id);

    public RouteResponseDTO create(RouteCreateDTO dto);

    public void delete(Long id);

    public RouteResponseDTO findOrCreate(RouteCreateDTO dto);
    
    public Route save(Route route); 
}