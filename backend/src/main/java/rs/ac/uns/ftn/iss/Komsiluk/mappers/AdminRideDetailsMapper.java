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
        dto.setStatus(ride.getStatus());
        dto.setRoute(routeMapper.toResponseDTO(ride.getRoute()));
        dto.setCreatedAt(ride.getCreatedAt());
        dto.setScheduledAt(ride.getScheduledAt());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
        dto.setCanceled(ride.getStatus() == RideStatus.CANCELLED);
        dto.setCancellationSource(ride.getCancellationSource());
        dto.setCancellationReason(ride.getCancellationReason());
        dto.setPrice(ride.getPrice());
        dto.setPanicTriggered(ride.isPanicTriggered());

        dto.setVehicleType(ride.getVehicleType());
        dto.setBabyFriendly(ride.isBabyFriendly());
        dto.setPetFriendly(ride.isPetFriendly());
        dto.setDistanceKm(ride.getDistanceKm());
        dto.setEstimatedDurationMin(ride.getEstimatedDurationMin());

        if (ride.getDriver() != null) {
            dto.setDriver(driverMapper.toResponseDTO(ride.getDriver()));
        }

        if (ride.getCreatedBy() != null) {
            dto.setCreatorId(ride.getCreatedBy().getId());
            dto.setCreatorEmail(ride.getCreatedBy().getEmail());
        }

        dto.setPassengerIds(
                ride.getPassengers()
                        .stream()
                        .map(User::getId)
                        .toList()
        );

        dto.setPassengerEmails(
                ride.getPassengers()
                        .stream()
                        .map(User::getEmail)
                        .toList()
        );

        dto.setRatings(ratings);
        dto.setInconsistencyReports(reports);

        return dto;
    }
}
