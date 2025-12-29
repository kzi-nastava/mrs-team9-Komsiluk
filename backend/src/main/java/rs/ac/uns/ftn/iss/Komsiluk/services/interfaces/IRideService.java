package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.time.LocalDate;
import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideLiveInfoDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;

public interface IRideService {

    public RideResponseDTO orderRide(RideCreateDTO dto);
    
    public RideResponseDTO startRide(Long rideId);

    public RideResponseDTO finishRide(Long rideId);

    public boolean userHasActiveRide(Long userId);

    public RideLiveInfoDTO getLiveInfo(Long rideId);

    public Collection<RideResponseDTO> getDriverRideHistory(Long driverId, LocalDate from, LocalDate to);
}