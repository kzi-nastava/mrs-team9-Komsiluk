package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;

@Repository
public class RouteRepository {

    private final Map<Long, Route> storage = new HashMap<>();
    private long nextId = 1L;

    public Collection<Route> findAll() {
        return storage.values();
    }

    public Optional<Route> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Route save(Route route) {
        if (route.getId() == null) {
            route.setId(nextId++);
        }
        storage.put(route.getId(), route);
        return route;
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    public Optional<Route> findBestByKey(String startAddress, String endAddress, String stops) {
        return storage.values().stream().filter(r -> Objects.equals(r.getStartAddress(), startAddress) && Objects.equals(r.getEndAddress(), endAddress) &&
                        Objects.equals(r.getStops(), stops)).sorted(Comparator.comparingInt(r ->  r.getEstimatedDurationMin() != null ? r.getEstimatedDurationMin() : Integer.MAX_VALUE)).findFirst();
    }
}