package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.List;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingResponseDTO;

public interface IRatingService {

    RatingResponseDTO createRating(Long rideId, RatingCreateDTO dto);

    List<RatingResponseDTO> getRatingsForRide(Long rideId);

    RatingResponseDTO getRatingForRideByRater(Long rideId, Long raterId);
}
