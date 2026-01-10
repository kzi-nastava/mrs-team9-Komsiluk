package rs.ac.uns.ftn.iss.Komsiluk.util;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;

@Component
public class MockUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public MockUserSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedUser("driver@test.com", "driver12345", UserRole.DRIVER, true);
        seedUser("passenger@test.com", "pass12345", UserRole.PASSENGER, false);
        seedUser("admin@test.com", "admin12345", UserRole.ADMIN, false);
    }

    private void seedUser(String email, String rawPassword, UserRole role, boolean withVehicle) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        user.setFirstName("Test");
        user.setLastName(role.name());
        user.setCity("Novi Sad");
        user.setAddress("Test Address");
        user.setPhoneNumber("+381600000000");
        user.setProfileImageUrl("/images/default.png");

        user.setRole(role);
        user.setActive(true);
        user.setBlocked(false);
        user.setCreatedAt(LocalDateTime.now());

        if (role == UserRole.DRIVER) {
            user.setDriverStatus(DriverStatus.ACTIVE);
        }

        if (withVehicle) {
            Vehicle vehicle = new Vehicle();
            vehicle.setModel("Test Model");
            vehicle.setType(VehicleType.STANDARD);
            vehicle.setLicencePlate("NS-123-AB");
            vehicle.setSeatCount(4);
            vehicle.setPetFriendly(false);
            vehicle.setBabyFriendly(true);
            user.setVehicle(vehicle);
        }

        userRepository.save(user);
    }
}