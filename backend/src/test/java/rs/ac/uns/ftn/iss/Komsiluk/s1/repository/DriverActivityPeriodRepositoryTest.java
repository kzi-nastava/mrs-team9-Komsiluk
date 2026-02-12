package rs.ac.uns.ftn.iss.Komsiluk.s1.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import jakarta.persistence.EntityManager;
import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverActivityPeriod;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.DriverActivityPeriodRepository;

@DataJpaTest
@ActiveProfiles("test")
class DriverActivityPeriodRepositoryTest {

    @Autowired
    private DriverActivityPeriodRepository repo;

    @Autowired
    private EntityManager entityManager;

    private static final LocalDateTime BASE = LocalDateTime.of(2026, 1, 1, 10, 0);

    @Test
    void findByDriver_returnsEmpty_whenDriverHasNoPeriods() {
        User driver = persistDriver("dap1@test.com");

        List<DriverActivityPeriod> result = repo.findByDriver(driver);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findByDriver_returnsOnlyPeriodsForThatDriver() {
        User driverA = persistDriver("dap2a@test.com");
        User driverB = persistDriver("dap2b@test.com");

        persistPeriod(driverA, BASE.minusHours(2), BASE.minusHours(1));
        persistPeriod(driverA, BASE.minusMinutes(50), BASE.minusMinutes(10));
        persistPeriod(driverB, BASE.minusHours(3), BASE.minusHours(2));

        List<DriverActivityPeriod> result = repo.findByDriver(driverA);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(p -> p.getDriver().getId().equals(driverA.getId())));
    }

    
    
    // ---------------- helpers ----------------

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

    private DriverActivityPeriod persistPeriod(User driver, LocalDateTime start, LocalDateTime end) {
        DriverActivityPeriod period = new DriverActivityPeriod();
        period.setDriver(driver);
        period.setStartTime(start);
        period.setEndTime(end);

        entityManager.persist(period);
        entityManager.flush();
        return period;
    }
}
