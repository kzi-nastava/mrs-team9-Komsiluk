package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;

@Repository
public class UserRepository {

    private final Map<Long, User> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public UserRepository(PasswordEncoder passwordEncoder) {
        initMockUsers(passwordEncoder);
    }

    private void initMockUsers(PasswordEncoder passwordEncoder) {

        // DRIVER
        User driver = createUser(
                "driver@test.com",
                "driver12345",
                UserRole.DRIVER,
                passwordEncoder
        );
        driver.setDriverStatus(DriverStatus.ACTIVE);
        storage.put(driver.getId(), driver);

        // PASSENGER
        User passenger = createUser(
                "passenger@test.com",
                "pass12345",
                UserRole.PASSENGER,
                passwordEncoder
        );
        storage.put(passenger.getId(), passenger);

        // ADMIN
        User admin = createUser(
                "admin@test.com",
                "admin12345",
                UserRole.ADMIN,
                passwordEncoder
        );
        storage.put(admin.getId(), admin);
    }

    private User createUser(
            String email,
            String rawPassword,
            UserRole role,
            PasswordEncoder passwordEncoder
    ) {
        User user = new User();
        user.setId(idGenerator.incrementAndGet());
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

        return user;
    }


    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        storage.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        return storage.get(id);
    }

    public Collection<User> findAll() {
        return storage.values();
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsByEmail(String email) {
        return storage.values().stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public User findByEmail(String email) {
        return storage.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }
}

