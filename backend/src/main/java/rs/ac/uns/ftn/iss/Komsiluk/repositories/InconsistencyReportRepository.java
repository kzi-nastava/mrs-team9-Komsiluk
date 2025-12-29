package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.InconsistencyReport;

@Repository
public class InconsistencyReportRepository {

    private final Map<Long, InconsistencyReport> storage = new HashMap<>();
    private long idSequence = 1L;

    public InconsistencyReport save(InconsistencyReport report) {
        if (report.getId() == null) {
            report.setId(idSequence++);
        }
        storage.put(report.getId(), report);
        return report;
    }

    public Optional<InconsistencyReport> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Collection<InconsistencyReport> findAll() {
        return storage.values();
    }

    public Collection<InconsistencyReport> findByRideId(Long rideId) {
        return storage.values().stream()
                .filter(r -> r.getRideId() != null && r.getRideId().equals(rideId))
                .collect(Collectors.toList());
    }
}
