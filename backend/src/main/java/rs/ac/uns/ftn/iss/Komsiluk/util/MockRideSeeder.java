package rs.ac.uns.ftn.iss.Komsiluk.util;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RouteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;

@Component
@Order(2)
public class MockRideSeeder implements CommandLineRunner {

    private final RideRepository rideRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;

    public MockRideSeeder(RideRepository rideRepository, RouteRepository routeRepository, UserRepository userRepository) {
        this.rideRepository = rideRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {

        // da ne seeda svaki put:
        if (rideRepository.count() > 0) return;

        User driver = userRepository.findByEmailIgnoreCase("driver@test.com");
        User passenger = userRepository.findByEmailIgnoreCase("passenger@test.com");

        Route route = new Route();
        route.setStartAddress("Bulevar Oslobodjenja 1");
        route.setEndAddress("Strazilovska 10");
        route.setStops(null);
        route.setDistanceKm(5.0);
        route.setEstimatedDurationMin(10); // <<< OVO ti je falilo (NOT NULL)
        route = routeRepository.save(route);

        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(30);
        LocalDateTime startTime = LocalDateTime.now().minusMinutes(25);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(15);

        Ride ride = new Ride();
        ride.setStatus(RideStatus.FINISHED);
        ride.setCreatedAt(createdAt);
        ride.setStartTime(startTime);
        ride.setEndTime(endTime);

        ride.setRoute(route);
        ride.setDriver(driver);
        ride.setCreatedBy(passenger);
        ride.setPassengers(List.of()); // ili List.of() ako hoćeš prazno

        ride.setVehicleType(driver.getVehicle().getType());
        ride.setBabyFriendly(driver.getVehicle().isBabyFriendly());
        ride.setPetFriendly(driver.getVehicle().isPetFriendly());

        ride.setDistanceKm(route.getDistanceKm());
        ride.setEstimatedDurationMin(route.getEstimatedDurationMin()); // Ride ima int, ali ovde je OK

        ride.setPanicTriggered(false);
        ride.setPrice(BigDecimal.valueOf(800));

        rideRepository.save(ride);
    }
}
