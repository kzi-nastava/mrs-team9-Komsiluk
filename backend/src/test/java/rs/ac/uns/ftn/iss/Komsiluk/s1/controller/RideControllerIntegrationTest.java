package rs.ac.uns.ftn.iss.Komsiluk.s1.controller;

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
import rs.ac.uns.ftn.iss.Komsiluk.repositories.InconsistencyReportRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.NotificationRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.PricingRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
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
        passenger.setCreatedAt(LocalDateTime.now());
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

    private RideCreateDTO validDto() {
    	
        RideCreateDTO dto = new RideCreateDTO();
        dto.setCreatorId(passenger.getId());
        dto.setStartAddress("Start Address 1");
        dto.setEndAddress("End Address 1");
        dto.setStops(null);
        dto.setDistanceKm(2.5);
        dto.setEstimatedDurationMin(10);
        dto.setStartLat(45.0);
        dto.setStartLng(19.0);
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setBabyFriendly(false);
        dto.setPetFriendly(false);
        dto.setPassengerEmails(null);
        dto.setScheduledAt(LocalDateTime.now().plusHours(2));
        
        return dto;
    }

    @Test
    @DisplayName("401 when no token (JWT filter)")
    void orderRide_returns401_whenNoToken() {
    	
        RideCreateDTO dto = validDto();

        ResponseEntity<String> res = restTemplate.postForEntity(
                "/api/rides",
                new HttpEntity<>(dto, new HttpHeaders()),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }
    
    @Test
    @DisplayName("401 when token is present but invalid")
    void orderRide_returns401_whenInvalidToken() {
    	
        RideCreateDTO dto = validDto();

        HttpHeaders headers = authHeaders("this.is.not.valid");
        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, headers),
                String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, res.getStatusCode());
    }

    @Test
    @DisplayName("403 when token is valid but role is not PASSENGER")
    void orderRide_returns403_whenRoleNotPassenger() {
    	
        User driverUser = userRepository.findByEmailIgnoreCase("driver@test.com");
        String driverToken = jwtService.generateAccessToken(driverUser);

        RideCreateDTO dto = validDto();

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(driverToken)),
                String.class
        );

        assertEquals(HttpStatus.FORBIDDEN, res.getStatusCode());
    }

    @Test
    @DisplayName("400 when DTO validation fails (e.g. missing startAddress)")
    void orderRide_returns400_whenValidationFails() {
    	
        RideCreateDTO dto = validDto();
        dto.setStartAddress(""); // @NotBlank

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(passengerToken)),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        assertNotNull(res.getBody());
    }

    @Test
    @DisplayName("201 Created when happy path")
    void orderRide_returns201_whenSuccess() {
    	
        RideCreateDTO dto = validDto();

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
        assertEquals(dto.isBabyFriendly(), body.isBabyFriendly());
        assertEquals(dto.isPetFriendly(), body.isPetFriendly());
        assertEquals(dto.getStartAddress(), body.getStartAddress());
        assertEquals(dto.getEndAddress(), body.getEndAddress());
        assertEquals(dto.getEstimatedDurationMin(), body.getEstimatedDurationMin());
        assertEquals(dto.getDistanceKm(), body.getDistanceKm());
        assertEquals(dto.getScheduledAt(), body.getScheduledAt());
    }
    
    @Test
    @DisplayName("400 when creator already has a blocking ride in same time window")
    void orderRide_returns400_whenConflictExists() {
    	
        RideCreateDTO dto1 = validDto();
        dto1.setScheduledAt(LocalDateTime.now().plusHours(5));

        ResponseEntity<RideResponseDTO> first = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto1, authHeaders(passengerToken)),
                RideResponseDTO.class
        );
        assertEquals(HttpStatus.CREATED, first.getStatusCode());

        RideCreateDTO dto2 = validDto();
        dto2.setScheduledAt(dto1.getScheduledAt().plusMinutes(1));

        ResponseEntity<String> second = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto2, authHeaders(passengerToken)),
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, second.getStatusCode());
        assertNotNull(second.getBody());
    }
    
    @Test
    @DisplayName("404 when pricing for vehicle type is missing")
    void orderRide_returns404_whenPricingMissing() {
    	
        pricingRepository.deleteAll();

        RideCreateDTO dto = validDto();

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(passengerToken)),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertNotNull(res.getBody());
    }
    
    @Test
    @DisplayName("404 when creatorId does not exist")
    void orderRide_returns404_whenCreatorNotFound() {
    	
        RideCreateDTO dto = validDto();
        dto.setCreatorId(999999L);

        ResponseEntity<String> res = restTemplate.exchange(
                "/api/rides",
                HttpMethod.POST,
                new HttpEntity<>(dto, authHeaders(passengerToken)),
                String.class
        );

        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        assertNotNull(res.getBody());
    }


}
