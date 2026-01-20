package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Rating;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRideId(Long rideId);

    Optional<Rating> findByRideIdAndRaterId(Long rideId, Long raterId);

    boolean existsByRideIdAndRaterId(Long rideId, Long raterId);
}
