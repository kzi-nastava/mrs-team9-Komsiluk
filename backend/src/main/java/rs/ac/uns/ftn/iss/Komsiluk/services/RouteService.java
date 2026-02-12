package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RouteDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RouteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRouteService;

@Service
public class RouteService implements IRouteService {

	@Autowired
    private RouteRepository routeRepository;
	@Autowired
    private RouteDTOMapper mapper;

    @Override
    public Collection<RouteResponseDTO> getAll() {
        return routeRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public RouteResponseDTO getById(Long id) {
        Route route = routeRepository.findById(id).orElseThrow(() -> new NotFoundException("Route not found"));
        return mapper.toResponseDTO(route);
    }

    @Override
    public RouteResponseDTO create(RouteCreateDTO dto) {
        Route route = mapper.fromCreateDTO(dto);
        Route saved = routeRepository.save(route);
        return mapper.toResponseDTO(saved);
    }

    @Override
    public void delete(Long id) {
        Route route = routeRepository.findById(id).orElseThrow(() -> new NotFoundException("Route not found"));
        routeRepository.deleteById(route.getId());
    }

    @Override
    public RouteResponseDTO findOrCreate(RouteCreateDTO dto) {
        return routeRepository.findBestByKey(dto.getStartAddress(), dto.getEndAddress(), dto.getStops(), PageRequest.of(0, 1)).stream().findFirst().map(mapper::toResponseDTO).orElseGet(() -> create(dto));
    }
    
    @Override
    public Route save(Route route) {
		return routeRepository.save(route);
	}
}