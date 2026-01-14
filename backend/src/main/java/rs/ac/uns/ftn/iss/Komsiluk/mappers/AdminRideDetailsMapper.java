package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.inconsistency.InconsistencyReportResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.AdminRideDetailsDTO;

import java.util.Collection;
import java.util.List;

@Component
public class AdminRideDetailsMapper {

    private final RouteDTOMapper routeMapper;
    private final DriverDTOMapper driverMapper;

    public AdminRideDetailsMapper(RouteDTOMapper routeMapper,
                                  DriverDTOMapper driverMapper) {
        this.routeMapper = routeMapper;
        this.driverMapper = driverMapper;
    }

    public AdminRideDetailsDTO toDto(
            Ride ride,
            List<RatingResponseDTO> ratings,
            Collection<InconsistencyReportResponseDTO> reports
    ) {
        AdminRideDetailsDTO dto = new AdminRideDetailsDTO();

        dto.setRideId(ride.getId());
        dto.setRoute(routeMapper.toResponseDTO(ride.getRoute()));
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
        dto.setCanceled(ride.getStatus() == RideStatus.CANCELLED);
        dto.setCancellationSource(ride.getCancellationSource());
        dto.setCancellationReason(ride.getCancellationReason());
        dto.setPrice(ride.getPrice());
        dto.setPanicTriggered(ride.isPanicTriggered());

        if (ride.getDriver() != null) {
            dto.setDriver(driverMapper.toResponseDTO(ride.getDriver()));
        }

        dto.setPassengerIds(
                ride.getPassengers()
                        .stream()
                        .map(User::getId)
                        .toList()
        );

        dto.setRatings(ratings);
        dto.setInconsistencyReports(reports);

        return dto;
    }
}

