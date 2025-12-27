package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;

@Repository
public class RideRepository {

    private final Map<Long, Ride> storage = new HashMap<>();
    private long idSequence = 1L;

    public Ride save(Ride ride) {
        if (ride.getId() == null) {
            ride.setId(idSequence++);
        }
        storage.put(ride.getId(), ride);
        return ride;
    }

    public Optional<Ride> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Collection<Ride> findAll() {
        return storage.values();
    }
}
