package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverActivityPeriod;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;

@Repository
public class DriverActivityPeriodRepository {

    private final Map<Long, DriverActivityPeriod> storage = new HashMap<>();
    private long nextId = 1L;

    public DriverActivityPeriod save(DriverActivityPeriod period) {
        if (period.getId() == null) {
            period.setId(nextId++);
        }
        storage.put(period.getId(), period);
        return period;
    }

    public Collection<DriverActivityPeriod> findAll() {
        return storage.values();
    }

    public List<DriverActivityPeriod> findByDriver(User driver) {
        return storage.values().stream().filter(p -> p.getDriver() != null && p.getDriver().getId().equals(driver.getId())).collect(Collectors.toList());
    }

    public List<DriverActivityPeriod> findByDriverAndEndAfter(User driver, LocalDateTime from) {
        return storage.values().stream().filter(p -> p.getDriver() != null && p.getDriver().getId().equals(driver.getId())).filter(p -> p.getEndTime() != null && !p.getEndTime().isBefore(from)).collect(Collectors.toList());
    }

    public void delete(Long id) {
        storage.remove(id);
    }

    public void deleteAll(Collection<Long> ids) {
        ids.forEach(storage::remove);
    }
}
