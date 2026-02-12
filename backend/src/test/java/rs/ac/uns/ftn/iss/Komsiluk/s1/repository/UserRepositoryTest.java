package rs.ac.uns.ftn.iss.Komsiluk.s1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import jakarta.persistence.EntityManager;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;
    
    private static final LocalDateTime BASE = LocalDateTime.of(2026, 1, 1, 10, 0);
    
    // ---------------- findByEmailIgnoreCase tests ----------------

    @Test
    void findByEmailIgnoreCase_returnsUser_whenEmailMatchesIgnoringCase() {
        User user = persistUser("User.Test@Example.com");

        User found = userRepository.findByEmailIgnoreCase("user.test@example.com");

        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
        assertEquals("User.Test@Example.com", found.getEmail());
    }

    @Test
    void findByEmailIgnoreCase_returnsUser_whenDifferentCasing() {
        User user = persistUser("aaaBBB@Example.com");

        User found = userRepository.findByEmailIgnoreCase("AAAbbb@example.COM");

        assertNotNull(found);
        assertEquals(user.getId(), found.getId());
    }
    
    @Test
    void findByEmailIgnoreCase_returnsNull_whenNoUser() {
        User found = userRepository.findByEmailIgnoreCase("doesnotexist@example.com");
        
        assertNull(found);
    }

    
    
    // ---------------- findAvailableDriversNoConflict tests ----------------

    @Test
    void findAvailableDriversNoConflict_returnsDriver_whenAllConditionsMatch_andNoConflicts() {
        User driver = persistDriver("driver1@test.com", DriverStatus.ACTIVE, true, false, persistVehicle(VehicleType.STANDARD, 4, true, true, "TT-111-TT"));

        LocalDateTime newStart = BASE.plusMinutes(30);
        LocalDateTime newEnd   = newStart.plusMinutes(20);

        List<User> result = userRepository.findAvailableDriversNoConflict(VehicleType.STANDARD.name(), 2, true, true, newStart, newEnd);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(u -> u.getId().equals(driver.getId())));
    }

    @Test
    void findAvailableDriversNoConflict_excludesDriver_whenVehicleTypeDoesNotMatch() {
        persistDriver("driver2@test.com", DriverStatus.ACTIVE, true, false, persistVehicle(VehicleType.LUXURY, 4, true, true, "TT-222-TT"));

        LocalDateTime newStart = BASE.plusMinutes(30);
        LocalDateTime newEnd   = newStart.plusMinutes(20);

        List<User> result = userRepository.findAvailableDriversNoConflict(VehicleType.STANDARD.name(), 1, false, false, newStart, newEnd);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAvailableDriversNoConflict_excludesDriver_whenSeatCountInsufficient() {
        persistDriver("driver3@test.com", DriverStatus.ACTIVE, true, false, persistVehicle(VehicleType.STANDARD, 2, false, false, "TT-333-TT"));

        LocalDateTime newStart = BASE.plusMinutes(30);
        LocalDateTime newEnd   = newStart.plusMinutes(20);

        List<User> result = userRepository.findAvailableDriversNoConflict(VehicleType.STANDARD.name(), 4, false, false, newStart, newEnd);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAvailableDriversNoConflict_excludesDriver_whenBlockedOrInactiveOrWrongStatus() {
        persistDriver("driver4@test.com", DriverStatus.ACTIVE, true, true, persistVehicle(VehicleType.STANDARD, 4, false, false, "TT-444-TT"));
        persistDriver("driver5@test.com", DriverStatus.ACTIVE, false, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "TT-555-TT"));
        persistDriver("driver6@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "TT-666-TT"));

        LocalDateTime newStart = BASE.plusMinutes(30);
        LocalDateTime newEnd   = newStart.plusMinutes(20);

        List<User> result = userRepository.findAvailableDriversNoConflict(VehicleType.STANDARD.name(), 1, false, false, newStart, newEnd);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAvailableDriversNoConflict_excludesDriver_whenHasOverlappingRide_ACTIVE_ASSIGNED_SCHEDULED() {
        User driver = persistDriver("driver7@test.com", DriverStatus.ACTIVE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "TT-777-TT"));

        LocalDateTime existingStart = BASE.plusMinutes(40);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);

        persistRideForDriver(driver, RideStatus.ACTIVE, existingStart, existingEnd);

        LocalDateTime newStart = BASE.plusMinutes(50);
        LocalDateTime newEnd   = newStart.plusMinutes(10);

        List<User> result = userRepository.findAvailableDriversNoConflict(VehicleType.STANDARD.name(), 1, false, false, newStart, newEnd);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAvailableDriversNoConflict_includesDriver_whenTouchingBoundary_endEqualsNewStart() {
        User driver = persistDriver("driver8@test.com", DriverStatus.ACTIVE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "TT-888-TT"));

        LocalDateTime existingStart = BASE.plusMinutes(40);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);
        
        persistRideForDriver(driver, RideStatus.SCHEDULED, existingStart, existingEnd);

        LocalDateTime newStart = existingEnd;
        LocalDateTime newEnd   = newStart.plusMinutes(20);

        List<User> result = userRepository.findAvailableDriversNoConflict(VehicleType.STANDARD.name(), 1, false, false, newStart, newEnd);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(u -> u.getId().equals(driver.getId())));
    }

    @Test
    void findAvailableDriversNoConflict_excludesDriver_whenWrongFriendlyFlags() {
        persistDriver("driver9@test.com", DriverStatus.ACTIVE, true, false, persistVehicle(VehicleType.STANDARD, 4, true, false, "TT-999-TT"));

        LocalDateTime newStart = BASE.plusMinutes(30);
        LocalDateTime newEnd   = newStart.plusMinutes(20);

        List<User> result = userRepository.findAvailableDriversNoConflict(VehicleType.STANDARD.name(), 2, true, true, newStart, newEnd);

        assertTrue(result.isEmpty());
    }
    
    
    
    // ---------------- findDriversFinishingSoon tests ----------------

    @Test
    void findDriversFinishingSoon_returnsDriver_whenActiveRideEndsWithinWindow_andAllFiltersMatch() {
        User driver = persistDriver("finish1@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, true, true, "FS-111"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(driver, RideStatus.ACTIVE, now.minusMinutes(5), now.plusMinutes(7));

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 2, true, true, now, finishBefore);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(u -> u.getId().equals(driver.getId())));
    }

    @Test
    void findDriversFinishingSoon_excludesDriver_whenRideEndsAfterFinishBefore() {
        User driver = persistDriver("finish2@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-222"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(driver, RideStatus.ACTIVE, now.minusMinutes(5), finishBefore.plusMinutes(1));

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 1, false, false, now, finishBefore);

        assertTrue(result.isEmpty());
    }

    @Test
    void findDriversFinishingSoon_excludesDriver_whenRideEndsBeforeNow() {
        User driver = persistDriver("finish3@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-333"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(driver, RideStatus.ACTIVE, now.minusMinutes(30), now.minusMinutes(1));

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 1, false, false, now, finishBefore);

        assertTrue(result.isEmpty());
    }

    @Test
    void findDriversFinishingSoon_excludesDriver_whenRideStatusIsNotACTIVE() {
        User driver = persistDriver("finish4@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-444"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(driver, RideStatus.SCHEDULED, now.minusMinutes(5), now.plusMinutes(5));

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 1, false, false, now, finishBefore);

        assertTrue(result.isEmpty());
    }

    @Test
    void findDriversFinishingSoon_includesDriver_whenEndEqualsNowBoundary() {
        User driver = persistDriver("finish5@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-555"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(driver, RideStatus.ACTIVE, now.minusMinutes(10), now);

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 1, false, false, now, finishBefore);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(u -> u.getId().equals(driver.getId())));
    }

    @Test
    void findDriversFinishingSoon_includesDriver_whenEndEqualsFinishBeforeBoundary() {
        User driver = persistDriver("finish6@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-666"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(driver, RideStatus.ACTIVE, now.minusMinutes(1), finishBefore);

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 1, false, false, now, finishBefore);

        assertFalse(result.isEmpty());
        assertTrue(result.stream().anyMatch(u -> u.getId().equals(driver.getId())));
    }

    @Test
    void findDriversFinishingSoon_excludesDriver_whenFriendlyFlagsDontMatch() {
        User driver = persistDriver("finish7@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.STANDARD, 4, true, false, "FS-777"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(driver, RideStatus.ACTIVE, now.minusMinutes(5), now.plusMinutes(5));

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 1, true, true, now, finishBefore);

        assertTrue(result.isEmpty());
    }

    @Test
    void findDriversFinishingSoon_excludesDriver_whenVehicleTypeOrSeatsDontMatch() {
        persistDriver("finish8@test.com", DriverStatus.IN_RIDE, true, false, persistVehicle(VehicleType.LUXURY, 2, false, false, "FS-888"));

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 4, false, false, now, finishBefore);

        assertTrue(result.isEmpty());
    }

    @Test
    void findDriversFinishingSoon_excludesDriver_whenDriverIsBlockedOrInactiveOrWrongDriverStatus() {
        User blocked = persistDriver("finish9@test.com", DriverStatus.IN_RIDE, true, true, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-999"));
        User inactive = persistDriver("finish10@test.com", DriverStatus.IN_RIDE, false, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-101"));
        User wrongStatus = persistDriver("finish11@test.com", DriverStatus.INACTIVE, true, false, persistVehicle(VehicleType.STANDARD, 4, false, false, "FS-102")); // ako imaš BLOCKED u enumu; ako nema, stavi neki status koji nije ACTIVE/IN_RIDE

        LocalDateTime now = BASE;
        LocalDateTime finishBefore = now.plusMinutes(10);

        persistRideForDriver(blocked, RideStatus.ACTIVE, now.minusMinutes(5), now.plusMinutes(5));
        persistRideForDriver(inactive, RideStatus.ACTIVE, now.minusMinutes(5), now.plusMinutes(5));
        persistRideForDriver(wrongStatus, RideStatus.ACTIVE, now.minusMinutes(5), now.plusMinutes(5));

        List<User> result = userRepository.findDriversFinishingSoon(VehicleType.STANDARD.name(), 1, false, false, now, finishBefore);

        assertTrue(result.isEmpty());
    }

    

    // ---------------- helpers ----------------

    private User persistUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);
        user.setBlocked(false);
        user.setRole(UserRole.PASSENGER);
        user.setCreatedAt(LocalDateTime.now());

        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
    
    private Vehicle persistVehicle(VehicleType type, int seatCount, boolean babyFriendly, boolean petFriendly, String licencePlate) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(type);
        vehicle.setSeatCount(seatCount);
        vehicle.setBabyFriendly(babyFriendly);
        vehicle.setPetFriendly(petFriendly);
        vehicle.setLicencePlate(licencePlate);
        vehicle.setModel("Test Model");

        entityManager.persist(vehicle);
        entityManager.flush();
        return vehicle;
    }

    private User persistDriver(String email, DriverStatus driverStatus, boolean active, boolean blocked, Vehicle vehicle) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setFirstName("Test");
        user.setLastName("Driver");
        user.setActive(active);
        user.setBlocked(blocked);
        user.setRole(UserRole.DRIVER);
        user.setDriverStatus(driverStatus);
        user.setVehicle(vehicle);

        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Ride persistRideForDriver(User driver, RideStatus status, LocalDateTime start, LocalDateTime end) {
        Ride r = new Ride();
        r.setStatus(status);
        r.setCreatedAt(start.minusMinutes(1));
        r.setScheduledAt(start);
        r.setStartTime(start);
        r.setEndTime(end);
        r.setCreatedBy(driver);
        r.setDriver(driver);
        r.setVehicleType(VehicleType.STANDARD);
        r.setPanicTriggered(false);
        r.setBabyFriendly(false);
        r.setPetFriendly(false);
        r.setDistanceKm(1.0);
        r.setEstimatedDurationMin((int) Duration.between(start, end).toMinutes());

        entityManager.persist(r);
        entityManager.flush();
        return r;
    }
}
