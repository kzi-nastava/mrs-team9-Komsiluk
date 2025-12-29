package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.time.LocalDate;
import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideLiveInfoDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.*;

public interface IRideService {

    public RideResponseDTO orderRide(RideCreateDTO dto);
    
    public RideResponseDTO startRide(Long rideId);

    public RideResponseDTO finishRide(Long rideId);

    public boolean userHasActiveRide(Long userId);

    public RideEstimateResponseDTO estimate(RideEstimateRequestDTO dto);

    public void cancelByDriver(Long rideId, DriverCancelRideDTO dto);

    public void cancelByPassenger(Long rideId, PassengerCancelRideDTO dto);

    public StopRideResponseDTO stopRide(Long id,StopRideRequestDTO dto);


    public RideLiveInfoDTO getLiveInfo(Long rideId);

    public Collection<RideResponseDTO> getDriverRideHistory(Long driverId, LocalDate from, LocalDate to);
}