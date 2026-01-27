package rs.ac.uns.ftn.iss.Komsiluk.dtos.ride;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;

import java.util.List;

public record RidePassengerActiveDTO(
        Long rideId,
        Long driverId,
        String driverFirstName,
        String driverLastName,
        String driverEmail,
        String startAddress,
        String endAddress,
        List<String> stops,
        RideStatus status
) {}
