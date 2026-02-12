package rs.ac.uns.ftn.iss.Komsiluk.s1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;

@DataJpaTest
@ActiveProfiles("test")
class RideRepositoryTest {

    @Autowired
    private RideRepository rideRepository;

    @Autowired
    private EntityManager entityManager;

    private static final List<String> STATUS_FILTER = List.of("SCHEDULED", "ASSIGNED", "ACTIVE");
    
    private static final LocalDateTime BASE = LocalDateTime.of(2030, 1, 1, 12, 0);
    
    // ---------------- tests for existsBlockingRideForCreator ----------------

    @Test
    void existsBlockingRideForCreator_returnsFalse_whenNoRides() {
        User creator = persistCreator("creator1@test.com");
        LocalDateTime newStart = BASE.plusMinutes(0);
        LocalDateTime newEnd   = newStart.plusMinutes(30);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), STATUS_FILTER, newStart, newEnd);

        assertFalse(hasConflict);
    }

    @Test
    void existsBlockingRideForCreator_returnsFalse_whenStatusNotInList_evenIfOverlaps() {
        User creator = persistCreator("creator2@test.com");
        LocalDateTime existingStart = BASE.plusMinutes(0);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);

        persistRideForCreator(creator, RideStatus.FINISHED, existingStart, existingEnd);

        LocalDateTime newStart = existingStart.plusMinutes(10);
        LocalDateTime newEnd   = newStart.plusMinutes(10);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), STATUS_FILTER, newStart, newEnd);

        assertFalse(hasConflict);
    }

    @Test
    void existsBlockingRideForCreator_returnsTrue_whenOverlaps() {
        User creator = persistCreator("creator3@test.com");
        LocalDateTime existingStart = BASE.plusMinutes(0);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);

        persistRideForCreator(creator, RideStatus.SCHEDULED, existingStart, existingEnd);

        LocalDateTime newStart = existingStart.plusMinutes(10);
        LocalDateTime newEnd   = newStart.plusMinutes(30);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), STATUS_FILTER, newStart, newEnd);

        assertTrue(hasConflict);
    }

    @Test
    void existsBlockingRideForCreator_returnsFalse_whenTouchingEndBoundary() {
        User creator = persistCreator("creator4@test.com");
        LocalDateTime existingStart = BASE.plusMinutes(0);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);

        persistRideForCreator(creator, RideStatus.ASSIGNED, existingStart, existingEnd);

        LocalDateTime newStart = existingEnd;
        LocalDateTime newEnd   = newStart.plusMinutes(30);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), STATUS_FILTER, newStart, newEnd);

        assertFalse(hasConflict);
    }

    @Test
    void existsBlockingRideForCreator_returnsFalse_whenTouchingStartBoundary() {
        User creator = persistCreator("creator5@test.com");
        LocalDateTime existingStart = BASE.plusMinutes(0);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);

        persistRideForCreator(creator, RideStatus.ACTIVE, existingStart, existingEnd);

        LocalDateTime newStart = existingStart.minusMinutes(30);
        LocalDateTime newEnd   = existingStart;

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), STATUS_FILTER, newStart, newEnd);

        assertFalse(hasConflict);
    }

    @Test
    void existsBlockingRideForCreator_returnsFalse_whenRideBelongsToDifferentCreator() {
        User creatorA = persistCreator("creatorA@test.com");
        User creatorB = persistCreator("creatorB@test.com");

        LocalDateTime existingStart = BASE.plusMinutes(0);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);

        persistRideForCreator(creatorB, RideStatus.SCHEDULED, existingStart, existingEnd);

        LocalDateTime newStart = existingStart.plusMinutes(10);
        LocalDateTime newEnd   = newStart.plusMinutes(10);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creatorA.getId(), STATUS_FILTER, newStart, newEnd);

        assertFalse(hasConflict);
    }

    @Test
    void existsBlockingRideForCreator_returnsTrue_whenExistingRideIsFullyInsideNewRide() {
        User creator = persistCreator("creator6@test.com");

        LocalDateTime existingStart = BASE.plusMinutes(0);
        LocalDateTime existingEnd   = existingStart.plusMinutes(30);

        persistRideForCreator(creator, RideStatus.SCHEDULED, existingStart, existingEnd);

        LocalDateTime newStart = existingStart.minusMinutes(60);
        LocalDateTime newEnd   = existingEnd.plusMinutes(60);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), STATUS_FILTER, newStart, newEnd);

        assertTrue(hasConflict);
    }

    @Test
    void existsBlockingRideForCreator_returnsTrue_whenNewRideIsFullyInsideExistingRide() {
        User creator = persistCreator("creator7@test.com");

        LocalDateTime existingStart = BASE.plusMinutes(0);
        LocalDateTime existingEnd   = existingStart.plusMinutes(120);

        persistRideForCreator(creator, RideStatus.ASSIGNED, existingStart, existingEnd);

        LocalDateTime newStart = existingStart.plusMinutes(10);
        LocalDateTime newEnd   = newStart.plusMinutes(20);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), STATUS_FILTER, newStart, newEnd);

        assertTrue(hasConflict);
    }
    
    
    
    // ---------------- tests for countScheduledForDriverFrom ----------------

    @Test
    void countScheduledForDriverFrom_returns0_whenNoScheduledRides() {
        User driver = persistDriver("driver1@test.com");

        long count = rideRepository.countScheduledForDriverFrom(driver.getId(), BASE);

        assertEquals(0, count);
    }

    @Test
    void countScheduledForDriverFrom_countsOnlyScheduledForDriver_andOnlyFromInclusive() {
        User creator = persistCreator("creator9@test.com");
        User driver = persistDriver("driver2@test.com");

        persistRideWithDriverAndSchedule(creator, driver, RideStatus.SCHEDULED, BASE.minusMinutes(1));
        persistRideWithDriverAndSchedule(creator, driver, RideStatus.SCHEDULED, BASE);
        persistRideWithDriverAndSchedule(creator, driver, RideStatus.SCHEDULED, BASE.plusMinutes(10));

        long count = rideRepository.countScheduledForDriverFrom(driver.getId(), BASE);

        assertEquals(2, count);
    }

    @Test
    void countScheduledForDriverFrom_doesNotCountOtherStatuses() {
        User creator = persistCreator("creator10@test.com");
        User driver = persistDriver("driver3@test.com");

        persistRideWithDriverAndSchedule(creator, driver, RideStatus.ASSIGNED,  BASE.plusMinutes(5));
        persistRideWithDriverAndSchedule(creator, driver, RideStatus.ACTIVE,    BASE.plusMinutes(10));
        persistRideWithDriverAndSchedule(creator, driver, RideStatus.FINISHED,  BASE.plusMinutes(15));
        persistRideWithDriverAndSchedule(creator, driver, RideStatus.SCHEDULED, BASE.plusMinutes(20));

        long count = rideRepository.countScheduledForDriverFrom(driver.getId(), BASE);

        assertEquals(1, count);
    }

    @Test
    void countScheduledForDriverFrom_doesNotCountScheduledRidesOfOtherDrivers() {
        User creator = persistCreator("creator11@test.com");
        User driverA = persistDriver("driverA@test.com");
        User driverB = persistDriver("driverB@test.com");

        persistRideWithDriverAndSchedule(creator, driverA, RideStatus.SCHEDULED, BASE.plusMinutes(10));
        persistRideWithDriverAndSchedule(creator, driverB, RideStatus.SCHEDULED, BASE.plusMinutes(20));
        persistRideWithDriverAndSchedule(creator, driverB, RideStatus.SCHEDULED, BASE.plusMinutes(30));

        long countA = rideRepository.countScheduledForDriverFrom(driverA.getId(), BASE);
        long countB = rideRepository.countScheduledForDriverFrom(driverB.getId(), BASE);

        assertEquals(1, countA);
        assertEquals(2, countB);
    }

    @Test
    void countScheduledForDriverFrom_ignoresNullScheduledAt() {
        User creator = persistCreator("creator12@test.com");
        User driver = persistDriver("driver4@test.com");

        persistRideWithDriverAndNullSchedule(creator, driver, RideStatus.SCHEDULED);
        persistRideWithDriverAndSchedule(creator, driver, RideStatus.SCHEDULED, BASE.plusMinutes(5));

        long count = rideRepository.countScheduledForDriverFrom(driver.getId(), BASE);

        assertEquals(1, count);
    }

    
    
    // ---------------- helpers ----------------

    private User persistCreator(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setActive(true);
        user.setBlocked(false);
        user.setRole(UserRole.PASSENGER);

        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Ride persistRideForCreator(User creator, RideStatus status, LocalDateTime start, LocalDateTime end) {
        Ride ride = new Ride();
        ride.setStatus(status);
        ride.setCreatedAt(start.minusMinutes(1));
        ride.setScheduledAt(start);
        ride.setStartTime(start);
        ride.setEndTime(end);
        ride.setCreatedBy(creator);

        ride.setVehicleType(VehicleType.STANDARD);
        ride.setPanicTriggered(false);
        ride.setBabyFriendly(false);
        ride.setPetFriendly(false);
        ride.setDistanceKm(3.5);
        ride.setEstimatedDurationMin((int) Duration.between(start, end).toMinutes());

        entityManager.persist(ride);
        entityManager.flush();
        return ride;
    }
    
    private User persistDriver(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("hash");
        user.setFirstName("Test");
        user.setLastName("Driver");
        user.setActive(true);
        user.setBlocked(false);
        user.setRole(UserRole.DRIVER);

        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private Ride persistRideWithDriverAndSchedule(User creator, User driver, RideStatus status, LocalDateTime scheduledAt) {
        LocalDateTime start = scheduledAt;
        LocalDateTime end   = start.plusMinutes(30);

        Ride ride= new Ride();
        ride.setStatus(status);
        ride.setCreatedAt(start.minusMinutes(1));
        ride.setScheduledAt(scheduledAt);
        ride.setStartTime(start);
        ride.setEndTime(end);
        ride.setCreatedBy(creator);
        ride.setDriver(driver);

        ride.setVehicleType(VehicleType.STANDARD);
        ride.setPanicTriggered(false);
        ride.setBabyFriendly(false);
        ride.setPetFriendly(false);
        ride.setDistanceKm(1.0);
        ride.setEstimatedDurationMin((int) Duration.between(start, end).toMinutes());

        entityManager.persist(ride);
        entityManager.flush();
        return ride;
    }

    private Ride persistRideWithDriverAndNullSchedule(User creator, User driver, RideStatus status) {
        LocalDateTime start = BASE.plusMinutes(1);
        LocalDateTime end   = start.plusMinutes(30);

        Ride ride= new Ride();
        ride.setStatus(status);
        ride.setCreatedAt(start.minusMinutes(1));
        ride.setScheduledAt(null);
        ride.setStartTime(start);
        ride.setEndTime(end);

        ride.setCreatedBy(creator);
        ride.setDriver(driver);

        ride.setVehicleType(VehicleType.STANDARD);
        ride.setPanicTriggered(false);
        ride.setBabyFriendly(false);
        ride.setPetFriendly(false);
        ride.setDistanceKm(1.0);
        ride.setEstimatedDurationMin((int) Duration.between(start, end).toMinutes());

        entityManager.persist(ride);
        entityManager.flush();
        return ride;
    }
}