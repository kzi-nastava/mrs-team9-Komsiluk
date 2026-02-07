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
import rs.ac.uns.ftn.iss.Komsiluk.beans.Rating;
import rs.ac.uns.ftn.iss.Komsiluk.beans.InconsistencyReport;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RouteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RatingRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.InconsistencyReportRepository;

@Component
@Order(2)
public class MockRideSeeder implements CommandLineRunner {

    private final RideRepository rideRepository;
    private final RouteRepository routeRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final InconsistencyReportRepository inconsistencyReportRepository;

    public MockRideSeeder(RideRepository rideRepository,
                          RouteRepository routeRepository,
                          UserRepository userRepository,
                          RatingRepository ratingRepository,
                          InconsistencyReportRepository inconsistencyReportRepository) {
        this.rideRepository = rideRepository;
        this.routeRepository = routeRepository;
        this.userRepository = userRepository;
        this.ratingRepository = ratingRepository;
        this.inconsistencyReportRepository = inconsistencyReportRepository;
    }

    @Override
    public void run(String... args) {
        if (rideRepository.count() > 0) return;

        User driver = userRepository.findByEmailIgnoreCase("driver@test.com");
        User driver3 = userRepository.findByEmailIgnoreCase("driver3@test.com");
        User passenger1 = userRepository.findByEmailIgnoreCase("passenger@test.com");
        User passenger2 = userRepository.findByEmailIgnoreCase("passenger2@test.com");
        User passenger3 = userRepository.findByEmailIgnoreCase("passenger3@test.com");

        // Prva vožnja
        Route route1 = new Route();
        route1.setStartAddress("Булевар ослобођења 1, МЗ Сава Ковачевић, Нови Сад");
        route1.setEndAddress("Стражиловска 10, МЗ Соња Маринковић, Нови Сад");
        route1.setStops(
                "Јеврејска 42, МЗ Прва војвођанска бригада, Нови Сад" + "|" +
                        "Илариона Руварца 1, Северни Телеп, МЗ Братство телеп, Нови Сад" + "|" +
                        "Трг Доситеја Обрадовића 7, Лиман, Нови Сад"
        );
        route1.setDistanceKm(7.5);
        route1.setEstimatedDurationMin(15);
        route1 = routeRepository.save(route1);

        LocalDateTime now = LocalDateTime.now();

        Ride ride1 = new Ride();
        ride1.setStatus(RideStatus.FINISHED);
        ride1.setCreatedAt(now.minusMinutes(40));
        ride1.setStartTime(now.minusMinutes(35));
        ride1.setEndTime(now.minusMinutes(20));
        ride1.setRoute(route1);
        ride1.setDriver(driver);
        ride1.setCreatedBy(passenger1);
        ride1.setPassengers(List.of(passenger1, passenger2, passenger3));
        ride1.setVehicleType(driver.getVehicle().getType());
        ride1.setBabyFriendly(driver.getVehicle().isBabyFriendly());
        ride1.setPetFriendly(driver.getVehicle().isPetFriendly());
        ride1.setDistanceKm(route1.getDistanceKm());
        ride1.setEstimatedDurationMin(route1.getEstimatedDurationMin());
        ride1.setPanicTriggered(false);
        ride1.setPrice(BigDecimal.valueOf(1200));
        ride1 = rideRepository.save(ride1);

        // Ratingi za prvu vožnju
        createRating(ride1, passenger1, driver, 4, 5, "Odlična vožnja, vozač vrlo ljubazan!");
        createRating(ride1, passenger2, driver, 5, 5, "Sve super, preporučujem!");

        // Druga vožnja - 21.1.2026
        Route route2 = new Route();
        route2.setStartAddress("Majevicka, МЗ Народни хероји, Novi Sad");
        route2.setEndAddress("Mise Dimitrijevica, МЗ 7. Јули, Novi Sad");
        route2.setStops(
                "Turgenjeva, МЗ Народни хероји, Novi Sad" + "|" +
                        "Cirpanova, МЗ Народни хероји, Novi Sad"
        );
        route2.setDistanceKm(3.2);
        route2.setEstimatedDurationMin(9);
        route2 = routeRepository.save(route2);

        LocalDateTime jan21_2026 = LocalDateTime.of(2026, 1, 21, 23, 58);

        Ride ride2 = new Ride();
        ride2.setStatus(RideStatus.FINISHED);
        ride2.setCreatedAt(jan21_2026.minusMinutes(15));
        ride2.setStartTime(jan21_2026);
        ride2.setEndTime(jan21_2026.plusMinutes(9));
        ride2.setRoute(route2);
        ride2.setDriver(driver3);
        ride2.setCreatedBy(passenger1);
        ride2.setPassengers(List.of(passenger2, passenger3));
        ride2.setVehicleType(driver3.getVehicle().getType());
        ride2.setBabyFriendly(driver3.getVehicle().isBabyFriendly());
        ride2.setPetFriendly(driver3.getVehicle().isPetFriendly());
        ride2.setDistanceKm(route2.getDistanceKm());
        ride2.setEstimatedDurationMin(route2.getEstimatedDurationMin());
        ride2.setPanicTriggered(true);
        ride2.setPrice(BigDecimal.valueOf(409));
        ride2 = rideRepository.save(ride2);

        // Ratingi za drugu vožnju
        createRating(ride2, passenger1, driver3, 2, 3, "Vozač je bio nestrpljiv i vozio brzo.");
        createRating(ride2, passenger3, driver3, 3, 2, "Automobil nije bio čist.");

        // Inconsistency reporti za drugu vožnju (zbog panic alarma)
        createInconsistencyReport(ride2, passenger1, UserRole.PASSENGER,
                "Vozač je vozio opasno brzo i ignorisao je molbe da uspori. Morala sam da pritisnem panic dugme.");

        createInconsistencyReport(ride2, driver3, UserRole.DRIVER,
                "Putnica je bila neprijatna i konstantno mi govorila kako da vozim. Panic alarm je pritisnut bez pravog razloga.");
    }

    private void createRating(Ride ride, User rater, User driver, Integer vehicleGrade, Integer driverGrade, String comment) {
        Rating rating = new Rating();
        rating.setRideId(ride.getId());
        rating.setRaterId(rater.getId());
        rating.setDriverId(driver.getId());
        rating.setVehicleId(driver.getVehicle().getId());
        rating.setVehicleGrade(vehicleGrade);
        rating.setDriverGrade(driverGrade);
        rating.setComment(comment);
        rating.setCreatedAt(ride.getEndTime().plusMinutes(5));
        ratingRepository.save(rating);
    }

    private void createInconsistencyReport(Ride ride, User reporter, UserRole reporterRole, String message) {
        InconsistencyReport report = new InconsistencyReport();
        report.setRide(ride);
        report.setReporter(reporter);
        report.setReporterRole(reporterRole);
        report.setMessage(message);
        report.setCreatedAt(ride.getEndTime().plusMinutes(2));
        inconsistencyReportRepository.save(report);
    }
}