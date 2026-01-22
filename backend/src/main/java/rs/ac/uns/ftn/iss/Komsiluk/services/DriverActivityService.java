package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverActivityPeriod;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.DriverActivityPeriodRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverActivityService;

@Service
public class DriverActivityService implements IDriverActivityService {

    private static final long MAX_MINUTES_LAST_24H = 8 * 60;

    @Autowired
    private DriverActivityPeriodRepository repo;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void startActivity(User driver) {
        DriverActivityPeriod open = findOpenPeriod(driver);
        if (open != null) {
            return;
        }

        DriverActivityPeriod period = new DriverActivityPeriod(null, LocalDateTime.now(), null, driver);
        repo.save(period);
    }

    @Override
    public void endActivity(User driver) {
        DriverActivityPeriod open = findOpenPeriod(driver);
        if (open == null) {
            return;
        }
        open.setEndTime(LocalDateTime.now());
        repo.save(open);
    }

    @Override
    public long getWorkedMinutesLast24h(User driver) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minusHours(24);

        List<DriverActivityPeriod> all = repo.findByDriver(driver);

        return all.stream().mapToLong(p -> {
                    LocalDateTime start = p.getStartTime();
                    LocalDateTime end   = p.getEndTime() != null ? p.getEndTime() : now;

                    if (end.isBefore(from)) {
                        return 0L;
                    }
                    if (start.isBefore(from)) {
                        start = from;
                    }
                    return Duration.between(start, end).toMinutes();
                })
                .sum();
    }
    
    @Override
    public long getWorkedMinutesLast24hAt(User driver, LocalDateTime ref) {
        LocalDateTime from = ref.minusHours(24);

        List<DriverActivityPeriod> all = repo.findByDriver(driver);

        return all.stream().mapToLong(p -> {
            LocalDateTime start = p.getStartTime();
            if (start == null) return 0L;

            LocalDateTime end = (p.getEndTime() != null) ? p.getEndTime() : ref;

            if (end.isBefore(from) || !start.isBefore(ref)) {
                return 0L;
            }

            if (start.isBefore(from)) start = from;
            if (end.isAfter(ref)) end = ref;

            if (!end.isAfter(start)) return 0L;

            return Duration.between(start, end).toMinutes();
        }).sum();
    }

    @Override
    public boolean canAcceptNewRide(User driver) {
        long minutes = getWorkedMinutesLast24h(driver);
        return minutes < MAX_MINUTES_LAST_24H;
    }
    
    @Override
    public boolean canAcceptNewRide(Long driverId) {
    	User driver = userRepository.findById(driverId).orElse(null);
        long minutes = getWorkedMinutesLast24h(driver);
        return minutes < MAX_MINUTES_LAST_24H;
    }
    
    @Override
    public boolean canAcceptNewRideAt(Long driverId, LocalDateTime rideStart, int rideDurationMin) {

        User driver = userRepository.findById(driverId).orElse(null);
        if (driver == null) return false;

        long workedMinutes = getWorkedMinutesLast24hAt(driver, rideStart);

        long afterThisRide = workedMinutes + rideDurationMin;

        return afterThisRide <= MAX_MINUTES_LAST_24H;
    }

    private DriverActivityPeriod findOpenPeriod(User driver) {
        return repo.findTopByDriverAndEndTimeIsNullOrderByStartTimeDesc(driver);
    }

    public void cleanupOld(User driver) {
        LocalDateTime from = LocalDateTime.now().minusHours(24);
        List<Long> oldIds = repo.findByDriver(driver).stream()
                .filter(p -> p.getEndTime() != null && p.getEndTime().isBefore(from))
                .map(DriverActivityPeriod::getId)
                .collect(Collectors.toList());
        
        repo.deleteAllById(oldIds);
    }
}