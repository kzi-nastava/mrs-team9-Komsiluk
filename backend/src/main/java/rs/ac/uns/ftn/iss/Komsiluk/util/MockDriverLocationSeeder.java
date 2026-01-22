package rs.ac.uns.ftn.iss.Komsiluk.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.DriverLocationRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;

import java.time.LocalDateTime;

@Component
@Order(3)
public class MockDriverLocationSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DriverLocationRepository driverLocationRepository;

    public MockDriverLocationSeeder(UserRepository userRepository, DriverLocationRepository driverLocationRepository) {
        this.userRepository = userRepository;
        this.driverLocationRepository = driverLocationRepository;
    }

    @Override
    public void run(String... args) {
        // ako ne želiš da se menja svaki restart:
        // if (driverLocationRepository.count() > 0) return;

        User d1 = userRepository.findByEmailIgnoreCase("driver@test.com");
        User d2 = userRepository.findByEmailIgnoreCase("driver2@test.com");
        User d3 = userRepository.findByEmailIgnoreCase("driver3@test.com");

        upsertLocation(d1.getId(), 45.2671, 19.8335); // centar
        upsertLocation(d2.getId(), 45.2540, 19.8420); // liman
        upsertLocation(d3.getId(), 45.2810, 19.8220); // detelinara
    }

    private void upsertLocation(Long driverId, double lat, double lng) {
        DriverLocation loc = driverLocationRepository
                .findByDriverId(driverId)
                .orElseGet(DriverLocation::new);

        loc.setDriverId(driverId);              // bitno ako je new
        loc.setLat(lat);
        loc.setLng(lng);
        loc.setUpdatedAt(LocalDateTime.now());

        driverLocationRepository.save(loc);
    }
}


