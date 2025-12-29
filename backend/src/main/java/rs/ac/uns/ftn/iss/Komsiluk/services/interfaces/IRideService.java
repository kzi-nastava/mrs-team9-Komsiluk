package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.*;

public interface IRideService {

    public RideResponseDTO orderRide(RideCreateDTO dto);
    
    public RideResponseDTO startRide(Long rideId);
    
    public boolean userHasActiveRide(Long userId);

    public RideEstimateResponseDTO estimate(RideEstimateRequestDTO dto);

    public void cancelByDriver(Long rideId, DriverCancelRideDTO dto);

    public void cancelByPassenger(Long rideId, PassengerCancelRideDTO dto);

    public StopRideResponseDTO stopRide(Long id,StopRideRequestDTO dto);

}