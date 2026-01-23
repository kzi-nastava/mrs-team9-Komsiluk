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
        if (rideRepository.count() > 0) return;

        User driver = userRepository.findByEmailIgnoreCase("driver@test.com");
        User passenger1 = userRepository.findByEmailIgnoreCase("passenger@test.com");
        User passenger2 = userRepository.findByEmailIgnoreCase("passenger2@test.com");
        User passenger3 = userRepository.findByEmailIgnoreCase("passenger3@test.com");

        Route route = new Route();
        route.setStartAddress("Булевар ослобођења 1, МЗ Сава Ковачевић, Нови Сад");
        route.setEndAddress("Стражиловска 10, МЗ Соња Маринковић, Нови Сад");

        route.setStops(
                "Јеврејска 42, МЗ Прва војвођанска бригада, Нови Сад" +"|"+
                "Илариона Руварца 1, Северни Телеп, МЗ Братство телеп, Нови Сад"+"|"+"Трг Доситеја Обрадовића 7, Лиман, Нови Сад"
        );

        route.setDistanceKm(7.5);
        route.setEstimatedDurationMin(15);
        route = routeRepository.save(route);

        LocalDateTime now = LocalDateTime.now();

        Ride ride = new Ride();
        ride.setStatus(RideStatus.FINISHED);
        ride.setCreatedAt(now.minusMinutes(40));
        ride.setStartTime(now.minusMinutes(35));
        ride.setEndTime(now.minusMinutes(20));

        ride.setRoute(route);
        ride.setDriver(driver);
        ride.setCreatedBy(passenger1);

        // DODAVANJE 2 DODATNA PASSENGERA
        // Lista sada sadrži sve putnike koji učestvuju u vožnji
        ride.setPassengers(List.of(passenger1, passenger2, passenger3));

        ride.setVehicleType(driver.getVehicle().getType());
        ride.setBabyFriendly(driver.getVehicle().isBabyFriendly());
        ride.setPetFriendly(driver.getVehicle().isPetFriendly());

        ride.setDistanceKm(route.getDistanceKm());
        ride.setEstimatedDurationMin(route.getEstimatedDurationMin());

        ride.setPanicTriggered(false);
        ride.setPrice(BigDecimal.valueOf(1200));

        rideRepository.save(ride);
    }
}
