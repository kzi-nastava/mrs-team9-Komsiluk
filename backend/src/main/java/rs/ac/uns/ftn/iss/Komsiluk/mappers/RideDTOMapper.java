package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;

@Component
public class RideDTOMapper {

    private final ModelMapper modelMapper;

    @Autowired
    public RideDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public RideResponseDTO toResponseDTO(Ride ride) {
        RideResponseDTO dto = modelMapper.map(ride, RideResponseDTO.class);

        dto.setRouteId(ride.getRoute().getId());
        dto.setDriverId(ride.getDriver() != null ? ride.getDriver().getId() : null);

        List<Long> pIds = new ArrayList<>();
        if (ride.getPassengers() != null) {
            for (User u : ride.getPassengers()) {
                pIds.add(u.getId());
            }
        }
        dto.setPassengerIds(pIds);

        dto.setStartAddress(ride.getRoute().getStartAddress());
        dto.setEndAddress(ride.getRoute().getEndAddress());
        dto.setStops(ride.getRoute().getStops() == null || ride.getRoute().getStops().isBlank() ? List.of() : Arrays.asList(ride.getRoute().getStops().split("\\|"))
        );

        return dto;
    }
}
