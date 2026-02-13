package rs.ac.uns.ftn.iss.Komsiluk.S2;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import rs.ac.uns.ftn.iss.Komsiluk.beans.*;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.*;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.RideService;
import rs.ac.uns.ftn.iss.Komsiluk.services.MailService;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RideDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
class FinishRideServiceTest {

    @Mock private RideRepository rideRepository;
    @Mock private UserRepository userRepository;
    @Mock private MailService mailService;
    @Mock private RideDTOMapper rideMapper;

    @Captor private ArgumentCaptor<Ride> rideCaptor;
    @Captor private ArgumentCaptor<String> emailCaptor;

    @InjectMocks
    private RideService rideService;

    private User driver;
    private User creator;

    private static final LocalDateTime BASE = LocalDateTime.of(2030,1,1,12,0);

    @BeforeEach
    void setUp() {
        driver = new User();
        driver.setId(10L);
        driver.setEmail("driver@test.com");
        driver.setDriverStatus(DriverStatus.IN_RIDE);

        creator = new User();
        creator.setId(20L);
        creator.setEmail("creator@test.com");
    }

    private Ride createRide(Long id, RideStatus status) {
        Ride ride = new Ride();
        ride.setId(id);
        ride.setStatus(status);
        ride.setCreatedAt(BASE.minusMinutes(30));
        ride.setStartTime(BASE.minusMinutes(20));
        ride.setPrice(new BigDecimal("550.00"));
        ride.setDriver(driver);
        ride.setCreatedBy(creator);
        ride.setPassengers(new ArrayList<>());
        return ride;
    }

    @Test
    @DisplayName("finishRide throws NotFoundException when ride does not exist")
    void finishRide_throwsNotFound_whenRideMissing() {

        Long rideId = 999L;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> rideService.finishRide(rideId));

        verify(rideRepository).findById(rideId);
        verifyNoInteractions(userRepository, mailService, rideMapper);
    }

    @Test
    @DisplayName("finishRide throws BadRequestException when ride not ACTIVE")
    void finishRide_throwsBadRequest_whenStatusNotActive() {

        Long rideId = 1L;
        Ride ride = createRide(rideId, RideStatus.ASSIGNED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        BadRequestException ex =
                assertThrows(BadRequestException.class,
                        () -> rideService.finishRide(rideId));

        assertEquals("Ride is not active", ex.getMessage());

        verify(rideRepository).findById(rideId);
        verifyNoInteractions(userRepository, mailService, rideMapper);
    }

    @Test
    @DisplayName("finishRide updates ride, driver and sends emails (happy path)")
    void finishRide_success() {

        Long rideId = 1L;
        Ride ride = createRide(rideId, RideStatus.ACTIVE);

        User p1 = new User(); p1.setEmail("p1@test.com");
        User p2 = new User(); p2.setEmail("p2@test.com");
        ride.setPassengers(List.of(p1, p2));

        RideResponseDTO mapped = new RideResponseDTO();

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(mapped);

        RideResponseDTO result = rideService.finishRide(rideId);

        assertNotNull(result);

        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();

        assertEquals(RideStatus.FINISHED, saved.getStatus());
        assertNotNull(saved.getEndTime());
        assertEquals(DriverStatus.ACTIVE, driver.getDriverStatus());

        verify(userRepository).save(driver);

        verify(mailService, times(3))
                .sendRideFinishedMail(emailCaptor.capture(), eq(rideId));

        List<String> sentEmails = emailCaptor.getAllValues();
        assertTrue(sentEmails.contains("creator@test.com"));
        assertTrue(sentEmails.contains("p1@test.com"));
        assertTrue(sentEmails.contains("p2@test.com"));

        verify(rideMapper).toResponseDTO(ride);
    }

    @Test
    @DisplayName("finishRide handles null passengers safely")
    void finishRide_handlesNullPassengers() {

        Long rideId = 2L;
        Ride ride = createRide(rideId, RideStatus.ACTIVE);
        ride.setPassengers(null);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(new RideResponseDTO());

        rideService.finishRide(rideId);

        verify(mailService, times(1))
                .sendRideFinishedMail("creator@test.com", rideId);

        verify(mailService, never())
                .sendRideFinishedMail(eq("p1@test.com"), anyLong());
    }

    @Test
    @DisplayName("finishRide throws NullPointerException when driver is null")
    void finishRide_throwsException_whenDriverNull() {

        Long rideId = 3L;
        Ride ride = createRide(rideId, RideStatus.ACTIVE);
        ride.setDriver(null);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        assertThrows(NullPointerException.class,
                () -> rideService.finishRide(rideId));

        verify(userRepository, never()).save(any());
    }
}
