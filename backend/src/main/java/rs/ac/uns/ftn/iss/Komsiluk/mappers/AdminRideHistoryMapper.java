package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.AdminRideHistoryDTO;

@Component
public class AdminRideHistoryMapper {


    public AdminRideHistoryDTO toDto(Ride ride) {
        AdminRideHistoryDTO dto = new AdminRideHistoryDTO();

        dto.setRideId(ride.getId());
        dto.setStartTime(ride.getStartTime());
        dto.setEndTime(ride.getEndTime());

        Route route = ride.getRoute();

        if (route != null) {
            dto.setStartAddress(extractStreetAndNumber(route.getStartAddress()));
            dto.setEndAddress(extractStreetAndNumber(route.getEndAddress()));

            dto.setRoute(route.toFormattedString());
        } else {
            dto.setRoute("");
        }
        dto.setPrice(ride.getPrice());
        dto.setPanicTriggered(ride.isPanicTriggered());
        dto.setCancellationSource(ride.getCancellationSource());
        dto.setCancellationReason(ride.getCancellationReason());

        return dto;
    }

    private String extractStreetAndNumber(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }
        return fullAddress.split(",")[0].trim();
    }


}
