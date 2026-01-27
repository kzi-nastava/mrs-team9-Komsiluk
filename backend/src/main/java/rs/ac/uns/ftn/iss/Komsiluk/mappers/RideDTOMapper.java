package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RidePassengerActiveDTO;
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
        dto.setCreatorId(ride.getCreatedBy().getId());
        dto.setStartAddress(ride.getRoute().getStartAddress());
        dto.setEndAddress(ride.getRoute().getEndAddress());
        dto.setStops(ride.getRoute().getStops() == null || ride.getRoute().getStops().isBlank() ? List.of() : Arrays.asList(ride.getRoute().getStops().split("\\|"))
        );

        return dto;
    }

    public RidePassengerActiveDTO toActiveResponseDTO(Ride ride) {
        String stopsStr = ride.getRoute().getStops();
        List<String> stopsList = (stopsStr == null || stopsStr.isBlank())
                ? List.of()
                : Arrays.asList(stopsStr.split("\\|"));

        return new RidePassengerActiveDTO(
                ride.getId(),
                ride.getDriver() != null ? ride.getDriver().getId() : null,
                ride.getDriver() != null ? ride.getDriver().getFirstName() : "N/A",
                ride.getDriver() != null ? ride.getDriver().getLastName() : "N/A",
                ride.getDriver() != null ? ride.getDriver().getEmail() : "N/A",
                ride.getRoute().getStartAddress(),
                ride.getRoute().getEndAddress(),
                stopsList,
                ride.getStatus()
        );
    }
}
