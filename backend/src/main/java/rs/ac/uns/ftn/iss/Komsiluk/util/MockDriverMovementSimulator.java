package rs.ac.uns.ftn.iss.Komsiluk.util;

import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional; // OBAVEZNO
import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.DriverLocationRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Random;

@Order(4)
@Component
public class MockDriverMovementSimulator {

    private final UserRepository userRepository;
    private final DriverLocationRepository driverLocationRepository;
    private static final double STEP = 0.0002;
    private final Random rnd = new Random();

    public MockDriverMovementSimulator(UserRepository userRepository,
                                       DriverLocationRepository driverLocationRepository) {
        this.userRepository = userRepository;
        this.driverLocationRepository = driverLocationRepository;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional // Dodato: garantuje da se promene upišu ispravno
    public void tick() {
        User d1 = userRepository.findByEmailIgnoreCase("driver@test.com");
        User d2 = userRepository.findByEmailIgnoreCase("driver2@test.com");
        User d3 = userRepository.findByEmailIgnoreCase("driver3@test.com");

        if (d1 != null) moveIfPresent(d1);
        if (d2 != null) moveIfPresent(d2);
        if (d3 != null) moveIfPresent(d3);
    }

    private void moveIfPresent(User d) {
        // Tražimo postojeću lokaciju, ako nema, tek onda pravimo novu
        DriverLocation loc = driverLocationRepository
                .findByDriverId(d.getId())
                .orElse(null);

        if (loc == null) {
            loc = new DriverLocation();
            loc.setDriverId(d.getId());
            loc.setLat(45.2671);
            loc.setLng(19.8335);
        }

        double dLat = (rnd.nextDouble() - 0.5) * 2 * STEP;
        double dLng = (rnd.nextDouble() - 0.5) * 2 * STEP;

        loc.setLat(clamp(loc.getLat() + dLat, 45.23, 45.30));
        loc.setLng(clamp(loc.getLng() + dLng, 19.78, 19.88));
        loc.setUpdatedAt(LocalDateTime.now());

        driverLocationRepository.save(loc); // Spring će sada znati da li je UPDATE ili INSERT
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
}