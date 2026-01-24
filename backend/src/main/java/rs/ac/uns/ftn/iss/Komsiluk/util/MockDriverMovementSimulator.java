package rs.ac.uns.ftn.iss.Komsiluk.util;

import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

    // "korak" ~ 0.0002 je oko 15-25m
    private static final double STEP = 0.0002;

    private final Random rnd = new Random();

    public MockDriverMovementSimulator(UserRepository userRepository,
                                       DriverLocationRepository driverLocationRepository) {
        this.userRepository = userRepository;
        this.driverLocationRepository = driverLocationRepository;
    }

    // menja lokacije svake 2 sekunde (promeni po potrebi)
    @Scheduled(fixedDelay = 2000)
    public void tick() {
        User d1 = userRepository.findByEmailIgnoreCase("driver@test.com");
        User d2 = userRepository.findByEmailIgnoreCase("driver2@test.com");
        User d3 = userRepository.findByEmailIgnoreCase("driver3@test.com");

        // Ako nijedan nije pronađen, nema smisla da radi
        if (d1 == null && d2 == null && d3 == null) {
            return;
        }

        moveIfPresent(d1);
        moveIfPresent(d2);
        moveIfPresent(d3);
    }

    private void moveIfPresent(User d) {
        if (d == null) return;

        DriverLocation loc = driverLocationRepository
                .findByDriverId(d.getId())
                .orElseGet(DriverLocation::new);

        // init ako je nov
        if (loc.getDriverId() == null) {
            loc.setDriverId(d.getId());
            loc.setLat(45.2671);
            loc.setLng(19.8335);
        }

        // mali “random walk”
        double dLat = (rnd.nextDouble() - 0.5) * 2 * STEP;
        double dLng = (rnd.nextDouble() - 0.5) * 2 * STEP;

        double newLat = loc.getLat() + dLat;
        double newLng = loc.getLng() + dLng;

        // clamp u granicama NS
        newLat = clamp(newLat, 45.214, 45.309);
        newLng = clamp(newLng, 19.764, 19.929);

        loc.setLat(newLat);
        loc.setLng(newLng);
        loc.setUpdatedAt(LocalDateTime.now());

        driverLocationRepository.save(loc);
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }
}
