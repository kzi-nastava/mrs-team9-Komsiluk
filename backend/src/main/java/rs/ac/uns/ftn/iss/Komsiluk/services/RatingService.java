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
        if (rideId == null || dto == null) throw new BadRequestException();
        if (dto.getRaterId() == null) throw new BadRequestException();
        if (dto.getDriverGrade() == null || dto.getVehicleGrade() == null) throw new BadRequestException();

        validateGrade(dto.getDriverGrade());
        validateGrade(dto.getVehicleGrade());

        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.FINISHED) {
            throw new BadRequestException();
        }

        if (ride.getEndTime() == null) throw new BadRequestException();
        if (LocalDateTime.now().isAfter(ride.getEndTime().plusDays(3))) {
            throw new BadRequestException();
        }

        if (!isRaterOnRide(ride, dto.getRaterId())) {
            throw new BadRequestException();
        }

        if (ratingRepository.existsByRideIdAndRaterId(rideId, dto.getRaterId())) {
            throw new AlreadyExistsException();
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
        return ratingMapper.toResponseDTO(rating);
    }

    @Override
    public List<RatingResponseDTO> getRatingsForRide(Long rideId) {
        rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        return ratingRepository.findByRideId(rideId).stream()
                .map(ratingMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RatingResponseDTO getRatingForRideByRater(Long rideId, Long raterId) {
        rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        Rating rating = ratingRepository.findByRideIdAndRaterId(rideId, raterId)
                .orElseThrow(NotFoundException::new);

        return ratingMapper.toResponseDTO(rating);
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
}
