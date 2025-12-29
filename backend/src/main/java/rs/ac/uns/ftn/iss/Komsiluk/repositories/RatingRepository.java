package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Rating;

@Repository
public class RatingRepository {

    private final Map<Long, Rating> storage = new HashMap<>();
    private long idSequence = 1L;

    public Rating save(Rating rating) {
        if (rating.getId() == null) {
            rating.setId(idSequence++);
        }
        storage.put(rating.getId(), rating);
        return rating;
    }

    public Optional<Rating> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Collection<Rating> findAll() {
        return storage.values();
    }

    public Collection<Rating> findByRideId(Long rideId) {
        return storage.values().stream()
                .filter(r -> r.getRideId() != null && r.getRideId().equals(rideId))
                .collect(Collectors.toList());
    }

    public Collection<Rating> findByRaterId(Long raterId) {
        return storage.values().stream()
                .filter(r -> r.getRaterId() != null && r.getRaterId().equals(raterId))
                .collect(Collectors.toList());
    }

    public boolean existsByRideIdAndRaterId(Long rideId, Long raterId) {
        return findByRideIdAndRaterId(rideId, raterId).isPresent();
    }

    public java.util.Optional<rs.ac.uns.ftn.iss.Komsiluk.beans.Rating> findByRideIdAndRaterId(Long rideId, Long raterId) {
        return storage.values().stream()
                .filter(r -> r.getRideId() != null && r.getRideId().equals(rideId)
                        && r.getRaterId() != null && r.getRaterId().equals(raterId))
                .findFirst();
    }

}
