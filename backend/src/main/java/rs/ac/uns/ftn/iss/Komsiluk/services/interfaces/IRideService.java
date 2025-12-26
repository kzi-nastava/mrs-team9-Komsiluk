package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;

public interface IRideService {

    RideResponseDTO orderRide(RideCreateDTO dto);
}