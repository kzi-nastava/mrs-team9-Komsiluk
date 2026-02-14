package rs.ac.uns.ftn.iss.Komsiluk.s3.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import org.springframework.test.context.ActiveProfiles;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Pricing;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.StopRideRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.*;
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class RideControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PricingRepository pricingRepository;
    @Autowired
    private RideRepository rideRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private VehicleRepository vehicleRepository;
    @Autowired
    private InconsistencyReportRepository inconsistencyReportRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private FavoriteRouteRepository favoriteRouteRepository;

    private User driver;
    private String driverToken;
    private Ride activeRide;

    @BeforeEach
    void setup() {
        inconsistencyReportRepository.deleteAll();
        rideRepository.deleteAll();
        notificationRepository.deleteAll();
        favoriteRouteRepository.deleteAll();
        routeRepository.deleteAll();
        userRepository.deleteAll();
        vehicleRepository.deleteAll();
        pricingRepository.deleteAll();

        Pricing pricing = new Pricing();
        pricing.setVehicleType(VehicleType.STANDARD);
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(50);
        pricingRepository.save(pricing);

        Vehicle vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setSeatCount(4);
        vehicle.setBabyFriendly(false);
        vehicle.setPetFriendly(false);
        vehicle.setLicencePlate("NS-TEST-1");
        vehicle.setModel("Golf 7");

        driver = new User();
        driver.setEmail("driver@test.com");
        driver.setPasswordHash("hash");
        driver.setFirstName("Marko");
        driver.setLastName("Markovic");
        driver.setActive(true);
        driver.setBlocked(false);
        driver.setRole(UserRole.DRIVER);
        driver.setDriverStatus(DriverStatus.IN_RIDE);
        driver.setVehicle(vehicle);
        driver = userRepository.save(driver);

        driverToken = jwtService.generateAccessToken(driver);

        User passenger = new User();
        passenger.setEmail("passenger@test.com");
        passenger.setPasswordHash("hash");
        passenger.setFirstName("Pera");
        passenger.setLastName("Peric");
        passenger.setActive(true);
        passenger.setBlocked(false);
        passenger.setRole(UserRole.PASSENGER);
        userRepository.save(passenger);

        Route route = new Route();
        route.setStartAddress("Startna 1");
        route.setStops("Usputna stanica 1|Usputna stanica 2");
        route.setEndAddress("Krajnja 1");
        route.setDistanceKm(10.0);
        route.setEstimatedDurationMin(15);
        route = routeRepository.save(route);

        activeRide = new Ride();
        activeRide.setStatus(RideStatus.ACTIVE);
        activeRide.setDriver(driver);
        activeRide.setCreatedBy(passenger);
        activeRide.setRoute(route);
        activeRide.setVehicleType(VehicleType.STANDARD);
        activeRide.setStartTime(LocalDateTime.now().minusMinutes(10));
        activeRide.setCreatedAt(LocalDateTime.now().minusMinutes(15));
        activeRide.setBabyFriendly(false);
        activeRide.setPetFriendly(false);
        activeRide.setDistanceKm(10.0);
        activeRide.setEstimatedDurationMin(15);
        activeRide.setPanicTriggered(false);

        activeRide = rideRepository.save(activeRide);
    }


    @Test
    @DisplayName("200 OK - Successfully stop ride")
    void stopRide_returns200_whenSuccess() {
        // arange
        StopRideRequestDTO dto = createValidStopRequest();

        // act
        ResponseEntity<RideResponseDTO> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                RideResponseDTO.class
        );

        //assert
        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertNotNull(res.getBody());

        RideResponseDTO body = res.getBody();
        assertEquals(RideStatus.FINISHED, body.getStatus());
        assertEquals("Bulevar Oslobodjenja 55", body.getEndAddress());
        assertEquals(0, body.getPrice().compareTo(java.math.BigDecimal.valueOf(350.0)));

        assertNotNull(body.getEndTime());
        assertTrue(body.getEndTime().isAfter(LocalDateTime.now().minusSeconds(30)),
                "End time should be recent");
        assertTrue(body.getEndTime().isBefore(LocalDateTime.now().plusSeconds(5)),
                "End time should not be in the future");

        assertNotNull(body.getStops());
        assertEquals(1, body.getStops().size());
        assertEquals("Usputna stanica 1", body.getStops().get(0));

        Ride updatedRide = rideRepository.findById(activeRide.getId()).orElseThrow();
        assertEquals(RideStatus.FINISHED, updatedRide.getStatus());

        assertNotNull(updatedRide.getRoute());
        assertEquals("Bulevar Oslobodjenja 55", updatedRide.getRoute().getEndAddress());
        assertEquals("Usputna stanica 1", updatedRide.getRoute().getStops());
    }

    @Test
    @DisplayName("401 Unauthorized - Distance is not positive")
    void stopRide_returns401_whenDistanceNotPositive() {
        // arrange
        StopRideRequestDTO dto = createValidStopRequest();
        dto.setDistanceTravelledKm(-1.5);

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    @DisplayName("401 Unauthorized - Stop address is blank")
    void stopRide_returns401_whenStopAddressBlank() {
        // arrange
        StopRideRequestDTO dto = createValidStopRequest();
        dto.setStopAddress("   ");

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    @DisplayName("401 Unauthorized - Too many visited stops")
    void stopRide_returns401_whenTooManyStops() {
        // arrange
        StopRideRequestDTO dto = createValidStopRequest();
        List<String> tooManyStops = new java.util.ArrayList<>();
        for (int i = 0; i < 11; i++) {
            tooManyStops.add("Stop " + i);
        }
        dto.setVisitedStops(tooManyStops);

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    @DisplayName("401 Unauthorized - Stop length too short")
    void stopRide_returns401_whenStopLengthInvalid() {
        // arrange
        StopRideRequestDTO dto = createValidStopRequest();
        dto.setVisitedStops(List.of("A"));

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }


    @Test
    @DisplayName("404 Not Found - Ride ID does not exist")
    void stopRide_returns404_whenRideNotFound() {
        // arrange
        StopRideRequestDTO dto = createValidStopRequest();

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/999999/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        // assert
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    @DisplayName("400 Bad Request - Ride is not ACTIVE (Business Logic)")
    void stopRide_returns400_whenRideNotActive() {
        // arrange
        activeRide.setStatus(RideStatus.FINISHED);
        rideRepository.save(activeRide);

        StopRideRequestDTO dto = createValidStopRequest();

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        // assert
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    @DisplayName("403 Forbidden - Passenger tries to stop ride")
    void stopRide_returns403_whenUserNotDriver() {
        // arrange
        User passenger = userRepository.findByEmailIgnoreCase("passenger@test.com");
        String passengerToken = jwtService.generateAccessToken(passenger);

        StopRideRequestDTO dto = createValidStopRequest();

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(passengerToken)),
                String.class
        );

        // assert
        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    @DisplayName("401 Unauthorized - No token")
    void stopRide_returns401_whenNoToken() {
        // arrange
        StopRideRequestDTO dto = createValidStopRequest();

        // act
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides/" + activeRide.getId() + "/stop",
                HttpMethod.POST,
                new HttpEntity<>(dto, new HttpHeaders()),
                String.class
        );

        // assert
        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    // helper
    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private StopRideRequestDTO createValidStopRequest() {
        StopRideRequestDTO dto = new StopRideRequestDTO();
        dto.setStopAddress("Bulevar Oslobodjenja 55");
        dto.setDistanceTravelledKm(5.0);
        dto.setVisitedStops(List.of("Usputna stanica 1"));
        return dto;
    }
}