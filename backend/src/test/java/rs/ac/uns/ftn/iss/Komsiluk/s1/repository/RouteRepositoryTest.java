package rs.ac.uns.ftn.iss.Komsiluk.s1.repository;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import jakarta.persistence.EntityManager;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RouteRepository;

@DataJpaTest
@ActiveProfiles("test")
class RouteRepositoryTest {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private EntityManager entityManager;

    private static final String START = "A";
    private static final String END = "B";
    private static final String STOPS = "S1|S2";

    @Test
    void findBestByKey_returnsEmpty_whenNoRoutesExist() {
        Optional<Route> found = routeRepository.findBestByKey(START, END, STOPS,PageRequest.of(0, 1)).stream().findFirst();
        
        assertTrue(found.isEmpty());
    }

    @Test
    void findBestByKey_returnsMatch_whenStopsMatchesExactly() {
        Route route = persistRoute(START, END, STOPS, 12.3, 25);

        Optional<Route> found = routeRepository.findBestByKey(START, END, STOPS, PageRequest.of(0, 1)).stream().findFirst();

        assertTrue(found.isPresent());
        assertTrue(found.get().getId().equals(route.getId()));
    }

    @Test
    void findBestByKey_returnsMatch_whenStopsParamIsNull_andRouteStopsIsNull() {
        Route renameNullStops = persistRoute(START, END, null, 5.0, 20);
        persistRoute(START, END, STOPS, 5.0, 10);

        Optional<Route> found = routeRepository.findBestByKey(START, END, null, PageRequest.of(0, 1)).stream().findFirst();

        assertTrue(found.isPresent());
        assertTrue(found.get().getId().equals(renameNullStops.getId()));
    }

    @Test
    void findBestByKey_returnsBestByEstimatedDurationMin_whenMultipleCandidates() {
        Route slow = persistRoute(START, END, STOPS, 10.0, 50);
        Route fast = persistRoute(START, END, STOPS, 10.0, 10);
        persistRoute("X", "Y", STOPS, 1.0, 1);

        Optional<Route> found = routeRepository.findBestByKey(START, END, STOPS, PageRequest.of(0, 1)).stream().findFirst();

        assertTrue(found.isPresent());
        assertTrue(found.get().getId().equals(fast.getId()));
        assertFalse(found.get().getId().equals(slow.getId()));
    }
    
    @Test
    void findBestByKey_returnsEmpty_whenStopsDifferent_evenIfStartEndSame() {
        persistRoute(START, END, STOPS, 10.0, 15);

        String differentStops = "S1|S3";
        Optional<Route> found = routeRepository.findBestByKey(START, END, differentStops, PageRequest.of(0, 1)).stream().findFirst();

        assertTrue(found.isEmpty());
    }

    

    // ---------------- helpers ----------------

    private Route persistRoute(String start, String end, String stops, double distanceKm, Integer estimatedDurationMin) {
        Route route = new Route();
        route.setStartAddress(start);
        route.setEndAddress(end);
        route.setStops(stops);
        route.setDistanceKm(distanceKm);
        route.setEstimatedDurationMin(estimatedDurationMin);

        entityManager.persist(route);
        entityManager.flush();
        return route;
    }
}
