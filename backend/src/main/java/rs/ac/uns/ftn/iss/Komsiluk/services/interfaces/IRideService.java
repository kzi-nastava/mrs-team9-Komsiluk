package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideEstimateRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideEstimateResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;

public interface IRideService {

    public RideResponseDTO orderRide(RideCreateDTO dto);
    
    public RideResponseDTO startRide(Long rideId);
    
    public boolean userHasActiveRide(Long userId);

    public RideEstimateResponseDTO estimate(RideEstimateRequestDTO dto);
}