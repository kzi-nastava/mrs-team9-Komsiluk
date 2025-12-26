package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;

@Component
public class RouteDTOMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public RouteDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // RouteCreateDTO -> Route
    public Route fromCreateDTO(RouteCreateDTO dto) {
        return modelMapper.map(dto, Route.class);
    }

    // Route -> RouteResponseDTO
    public RouteResponseDTO toResponseDTO(Route route) {
        return modelMapper.map(route, RouteResponseDTO.class);
    }
    
    // RouteResponseDTO -> Route
    public Route fromResponseDTO(RouteResponseDTO dto) {
		return modelMapper.map(dto, Route.class);
	}
 }