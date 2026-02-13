package rs.ac.uns.ftn.iss.Komsiluk.S2;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

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
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.*;
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class FinishRideControllerTest {

    private static final LocalDateTime FIXED_TIME =
            LocalDateTime.of(2026, 2, 12, 12, 0, 0);

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
    private InconsistencyReportRepository inconsistencyReportRepository;
    @Autowired
    private NotificationRepository notificationRepository;

    private User passenger;
    private String passengerToken;

    @BeforeEach
    void setup() {

        inconsistencyReportRepository.deleteAll();
        rideRepository.deleteAll();
        notificationRepository.deleteAll();
        userRepository.deleteAll();
        pricingRepository.deleteAll();

        passenger = new User();
        passenger.setEmail("passenger@test.com");
        passenger.setPasswordHash("hash");
        passenger.setFirstName("Test");
        passenger.setLastName("Passenger");
        passenger.setActive(true);
        passenger.setBlocked(false);
        passenger.setRole(UserRole.PASSENGER);
        passenger.setCreatedAt(FIXED_TIME);
        passenger = userRepository.save(passenger);

        passengerToken = jwtService.generateAccessToken(passenger);

        Pricing pricing = new Pricing();
        pricing.setVehicleType(VehicleType.STANDARD);
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(10);
        pricingRepository.save(pricing);

        Vehicle vehicle = new Vehicle();
        vehicle.setType(VehicleType.STANDARD);
        vehicle.setSeatCount(4);
        vehicle.setBabyFriendly(false);
        vehicle.setPetFriendly(false);
        vehicle.setLicencePlate("IT-123-IT");
        vehicle.setModel("Test Model");

        User driver = new User();
        driver.setEmail("driver@test.com");
        driver.setPasswordHash("hash");
        driver.setFirstName("Test");
        driver.setLastName("Driver");
        driver.setActive(true);
        driver.setBlocked(false);
        driver.setRole(UserRole.DRIVER);
        driver.setDriverStatus(DriverStatus.ACTIVE);
        driver.setVehicle(vehicle);

        userRepository.save(driver);
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        return headers;
    }

    private RideCreateDTO validDto(LocalDateTime scheduledAt) {

        RideCreateDTO dto = new RideCreateDTO();
        dto.setCreatorId(passenger.getId());
        dto.setStartAddress("Start Address 1");
        dto.setEndAddress("End Address 1");
        dto.setDistanceKm(2.5);
        dto.setEstimatedDurationMin(10);
        dto.setStartLat(45.0);
        dto.setStartLng(19.0);
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setBabyFriendly(false);
        dto.setPetFriendly(false);
        dto.setScheduledAt(scheduledAt);

        return dto;
    }

    @Test
    @DisplayName("401 when no token")
    void orderRide_returns401_whenNoToken() {

        RideCreateDTO dto = validDto(FIXED_TIME.plusHours(2));

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/rides",
                new HttpEntity<>(dto),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    @DisplayName("401 when invalid token")
    void orderRide_returns401_whenInvalidToken() {

        RideCreateDTO dto = validDto(FIXED_TIME.plusHours(2));

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders("invalid.token")),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    @DisplayName("403 when role is not PASSENGER")
    void orderRide_returns403_whenRoleNotPassenger() {

        User driverUser = userRepository.findByEmailIgnoreCase("driver@test.com");
        String driverToken = jwtService.generateAccessToken(driverUser);

        RideCreateDTO dto = validDto(FIXED_TIME.plusHours(2));

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    @DisplayName("400 when validation fails")
    void orderRide_returns400_whenValidationFails() {

        RideCreateDTO dto = validDto(FIXED_TIME.plusHours(2));
        dto.setStartAddress("");

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(passengerToken)),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    @DisplayName("201 when success")
    void orderRide_returns201_whenSuccess() {

        LocalDateTime scheduled = FIXED_TIME.plusHours(2);
        RideCreateDTO dto = validDto(scheduled);

        ResponseEntity<RideResponseDTO> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(passengerToken)),
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, res.getStatusCode());

        RideResponseDTO body = res.getBody();
        assertNotNull(body);

        assertEquals(dto.getCreatorId(), body.getCreatorId());
        assertEquals(dto.getVehicleType(), body.getVehicleType());
        assertEquals(dto.getStartAddress(), body.getStartAddress());
        assertEquals(dto.getEndAddress(), body.getEndAddress());
        assertEquals(dto.getDistanceKm(), body.getDistanceKm());

        // sigurnije poređenje vremena
        assertEquals(scheduled.withNano(0), body.getScheduledAt().withNano(0));
    }

    @Test
    @DisplayName("400 when conflicting ride exists")
    void orderRide_returns400_whenConflictExists() {

        LocalDateTime scheduled = FIXED_TIME.plusHours(5);

        RideCreateDTO dto1 = validDto(scheduled);

        ResponseEntity<RideResponseDTO> first = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto1, authHeaders(passengerToken)),
                RideResponseDTO.class
        );

        assertEquals(HttpStatus.CREATED, first.getStatusCode());

        RideCreateDTO dto2 = validDto(scheduled.plusMinutes(1));

        ResponseEntity<String> second = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto2, authHeaders(passengerToken)),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, second.getStatusCode());
    }
}
