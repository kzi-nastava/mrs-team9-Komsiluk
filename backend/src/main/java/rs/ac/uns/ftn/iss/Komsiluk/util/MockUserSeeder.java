package rs.ac.uns.ftn.iss.Komsiluk.util;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;

@Component
@Order(1)
public class MockUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MockUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUser("driver@test.com", "driver12345", UserRole.DRIVER, true,"NS-123-AB");
        seedUser("driver2@test.com", "driver12345", UserRole.DRIVER, true, "NS-102-AA");
        seedUser("driver3@test.com", "driver12345", UserRole.DRIVER, true, "NS-103-AA");
        seedUser("passenger@test.com", "pass12345", UserRole.PASSENGER, false,null);
        seedUser("passenger2@test.com", "pass12345", UserRole.PASSENGER, false,null);
        seedUser("passenger3@test.com", "pass12345", UserRole.PASSENGER, false,null);
        seedUser("admin@test.com", "admin12345", UserRole.ADMIN, false,null);
    }

    private void seedUser(String email, String rawPassword, UserRole role, boolean withVehicle, String plate) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        user.setFirstName("Test");
        user.setLastName(role.name());
        if (role == UserRole.DRIVER && user.getEmail().equalsIgnoreCase("driver@test.com")) {
            user.setFirstName("Uros");
            user.setLastName("Milinovic");
        }
        else if (role == UserRole.DRIVER && user.getEmail().equalsIgnoreCase("driver2@test.com")) {
            user.setFirstName("Branislav");
            user.setLastName("Markovic");
        }
        else if (role == UserRole.DRIVER && user.getEmail().equalsIgnoreCase("driver3@test.com")) {
            user.setFirstName("Nikola");
            user.setLastName("Savic");
        }

        user.setCity("Novi Sad");
        user.setAddress("Test Address");
        user.setPhoneNumber("+381600000000");
        user.setProfileImageUrl("/images/default.png");

        user.setRole(role);
        user.setActive(true);
        user.setBlocked(false);
        user.setCreatedAt(LocalDateTime.now());

        if (role == UserRole.DRIVER && user.getEmail().equalsIgnoreCase("driver@test.com")) {
            user.setDriverStatus(DriverStatus.ACTIVE);
        }
        else if (role == UserRole.DRIVER && user.getEmail().equalsIgnoreCase("driver2@test.com")) {
            user.setDriverStatus(DriverStatus.IN_RIDE);
        }
        else if (role == UserRole.DRIVER && user.getEmail().equalsIgnoreCase("driver3@test.com")) {
            user.setDriverStatus(DriverStatus.INACTIVE);
        }

        if (withVehicle) {
            Vehicle vehicle = new Vehicle();
            vehicle.setModel("Test Model");
            vehicle.setType(VehicleType.STANDARD);
            vehicle.setLicencePlate(plate);
            vehicle.setSeatCount(4);
            vehicle.setPetFriendly(false);
            vehicle.setBabyFriendly(true);
            user.setVehicle(vehicle);
        }

        userRepository.save(user);
    }
}