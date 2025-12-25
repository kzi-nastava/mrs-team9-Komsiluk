package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;

@Repository
public class VehicleRepository {

	private final Map<Long, Vehicle> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Vehicle save(Vehicle v) {
        if (v.getId() == null) {
            v.setId(idGenerator.getAndIncrement());
        }
        storage.put(v.getId(), v);
        return v;
    }

    public Optional<Vehicle> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Vehicle> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsByLicencePlate(String licencePlate) {
        return storage.values().stream().anyMatch(v -> v.getLicencePlate().equalsIgnoreCase(licencePlate));
    }
}
