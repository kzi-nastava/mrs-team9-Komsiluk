package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Rating;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RatingDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RatingRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.AlreadyExistsException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRatingService;

@Service
public class RatingService implements IRatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private RatingDTOMapper ratingMapper;

    @Override
    public RatingResponseDTO createRating(Long rideId, RatingCreateDTO dto) {
        if (rideId == null || dto == null) throw new BadRequestException("Ride ID and rating data must be provided");
        if (dto.getRaterId() == null) throw new BadRequestException("Rater ID must be provided");
        if (dto.getDriverGrade() == null || dto.getVehicleGrade() == null) throw new BadRequestException("Grades must be provided");

        validateGrade(dto.getDriverGrade());
        validateGrade(dto.getVehicleGrade());

        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.FINISHED) {
            throw new BadRequestException("Cannot rate a ride that is not finished");
        }

        if (ride.getEndTime() == null) throw new BadRequestException();
        if (LocalDateTime.now().isAfter(ride.getEndTime().plusDays(3))) {
            throw new BadRequestException("Rating period has expired. You can only rate within 3 days after the ride has ended.");
        }

        if (!isRaterOnRide(ride, dto.getRaterId())) {
            throw new BadRequestException("Rater must be a passenger or the driver of the ride");
        }

        if (ratingRepository.existsByRideIdAndRaterId(rideId, dto.getRaterId())) {
            throw new AlreadyExistsException("Rating from this rater for this ride already exists");
        }

        Rating rating = new Rating();
        rating.setRideId(rideId);
        rating.setRaterId(dto.getRaterId());

        if (ride.getDriver() != null) {
            rating.setDriverId(ride.getDriver().getId());
            if (ride.getDriver().getVehicle() != null) {
                rating.setVehicleId(ride.getDriver().getVehicle().getId());
            }
        }

        rating.setDriverGrade(dto.getDriverGrade());
        rating.setVehicleGrade(dto.getVehicleGrade());
        rating.setComment(dto.getComment());
        rating.setCreatedAt(LocalDateTime.now());

        ratingRepository.save(rating);

        // Mapiranje i popunjavanje email-a
        RatingResponseDTO responseDTO = ratingMapper.toResponseDTO(rating);
        responseDTO.setRaterMail(findRaterEmail(ride, dto.getRaterId()));

        return responseDTO;
    }

    @Override
    public List<RatingResponseDTO> getRatingsForRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        List<Rating> ratings = ratingRepository.findByRideId(rideId);

        return ratings.stream()
                .map(rating -> {
                    RatingResponseDTO dto = ratingMapper.toResponseDTO(rating);
                    // Popunjavamo email za svaki rating u listi
                    dto.setRaterMail(findRaterEmail(ride, rating.getRaterId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public RatingResponseDTO getRatingForRideByRater(Long rideId, Long raterId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        Rating rating = ratingRepository.findByRideIdAndRaterId(rideId, raterId)
                .orElseThrow(NotFoundException::new);

        RatingResponseDTO dto = ratingMapper.toResponseDTO(rating);
        dto.setRaterMail(findRaterEmail(ride, raterId));

        return dto;
    }

    private void validateGrade(Integer grade) {
        if (grade < 1 || grade > 5) {
            throw new BadRequestException();
        }
    }

    private boolean isRaterOnRide(Ride ride, Long raterId) {
        if (ride.getPassengers() != null) {
            for (User u : ride.getPassengers()) {
                if (u != null && Objects.equals(u.getId(), raterId)) return true;
            }
        }
        if (ride.getCreatedBy() != null && Objects.equals(ride.getCreatedBy().getId(), raterId)) {
            return true;
        }
        return false;
    }

    // Pomoćna metoda za pronalaženje emaila na osnovu raterId unutar vožnje
    private String findRaterEmail(Ride ride, Long raterId) {
        if (ride.getPassengers() != null) {
            for (User u : ride.getPassengers()) {
                if (u != null && Objects.equals(u.getId(), raterId)) {
                    return u.getEmail();
                }
            }
        }
        if (ride.getCreatedBy() != null && Objects.equals(ride.getCreatedBy().getId(), raterId)) {
            return ride.getCreatedBy().getEmail();
        }
        return null;
    }
}