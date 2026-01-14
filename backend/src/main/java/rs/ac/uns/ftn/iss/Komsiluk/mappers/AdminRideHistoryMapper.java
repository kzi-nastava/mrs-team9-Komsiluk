package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.AdminRideHistoryDTO;

@Component
public class AdminRideHistoryMapper {

    public AdminRideHistoryDTO toDto(Ride ride) {
        AdminRideHistoryDTO dto = new AdminRideHistoryDTO();

        dto.setRideId(ride.getId());

        if (ride.getRoute() != null) {
            dto.setStartAddress(ride.getRoute().getStartAddress());
            dto.setEndAddress(ride.getRoute().getEndAddress());
        }

        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());
        dto.setCanceled(ride.getStatus() == RideStatus.CANCELLED);
        dto.setCancellationSource(ride.getCancellationSource());
        dto.setPrice(ride.getPrice());
        dto.setPanicTriggered(ride.isPanicTriggered());

        return dto;
    }
}
