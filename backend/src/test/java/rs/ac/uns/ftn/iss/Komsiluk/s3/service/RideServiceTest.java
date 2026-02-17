package rs.ac.uns.ftn.iss.Komsiluk.s3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Pricing;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.StopRideRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.DriverActivityService;
import rs.ac.uns.ftn.iss.Komsiluk.services.NotificationService;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RideDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.PricingRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RouteRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.RideService;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PricingRepository pricingRepository;

    @Mock
    private DriverActivityService driverActivityService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RideDTOMapper rideMapper;

    @InjectMocks
    private RideService rideService;

    @Captor
    private ArgumentCaptor<Ride> rideCaptor;


    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    @DisplayName("Should stop ride and set driver to ACTIVE when logout is NOT pending")
    void stopRide_Success_DriverActive() {
        // arrange
        Long rideId = 1L;
        Ride ride = createActiveRide(rideId, 10L, 99L, VehicleType.STANDARD);
        ride.getDriver().setLogoutPending(false);

        Pricing pricing = createPricing(VehicleType.STANDARD, 100, 50);
        StopRideRequestDTO requestDTO = createStopRequest("Stop Adresa", 5.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(pricingRepository.findByVehicleType(VehicleType.STANDARD)).thenReturn(Optional.of(pricing));
        when(rideMapper.toResponseDTO(any())).thenReturn(new RideResponseDTO());

        // act
        rideService.stopRide(rideId, requestDTO);

        // assert
        verify(rideRepository).save(rideCaptor.capture());
        assertEquals(RideStatus.FINISHED, rideCaptor.getValue().getStatus());

        verify(userRepository).save(userCaptor.capture());
        User savedDriver = userCaptor.getValue();
        assertEquals(DriverStatus.ACTIVE, savedDriver.getDriverStatus());

        verifyNoInteractions(driverActivityService);
    }

    @Test
    @DisplayName("Should stop ride, set driver to INACTIVE and end activity when logout IS pending")
    void stopRide_Success_DriverLogout() {
        // arrange
        Long rideId = 1L;
        Ride ride = createActiveRide(rideId, 10L, 99L, VehicleType.STANDARD);
        User driver = ride.getDriver();
        driver.setLogoutPending(true);

        Pricing pricing = createPricing(VehicleType.STANDARD, 100, 50);
        StopRideRequestDTO requestDTO = createStopRequest("Stop Adresa", 5.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(pricingRepository.findByVehicleType(VehicleType.STANDARD)).thenReturn(Optional.of(pricing));
        when(rideMapper.toResponseDTO(any())).thenReturn(new RideResponseDTO());

        // act
        rideService.stopRide(rideId, requestDTO);

        // assert
        verify(driverActivityService).endActivity(driver);

        verify(userRepository).save(userCaptor.capture());
        User savedDriver = userCaptor.getValue();
        assertEquals(DriverStatus.INACTIVE, savedDriver.getDriverStatus());
        assertFalse(savedDriver.isLogoutPending());

        verify(notificationService, atLeastOnce()).createNotification(any());
    }

    @Test
    @DisplayName("Should NOT save anything when pricing is missing")
    void stopRide_PricingNotFound_NoPartialSave() {
        // arrange
        Long rideId = 1L;
        Ride ride = createActiveRide(rideId, 10L, 99L, VehicleType.STANDARD);
        StopRideRequestDTO requestDTO = createStopRequest("Adresa", 5.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(pricingRepository.findByVehicleType(VehicleType.STANDARD)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(NotFoundException.class, () ->
                rideService.stopRide(rideId, requestDTO)
        );

        verify(routeRepository, never()).save(any());
        verify(rideRepository, never()).save(any());
        verify(userRepository, never()).save(any());
        verifyNoInteractions(notificationService);
    }

    @Test
    @DisplayName("Should throw BadRequestException when ride is not ACTIVE")
    void stopRide_RideNotActive() {
        // arrange
        Long rideId = 1L;
        Ride ride = createActiveRide(rideId, 10L, 99L, VehicleType.STANDARD);
        ride.setStatus(RideStatus.FINISHED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        // act and assert
        assertThrows(BadRequestException.class, () -> rideService.stopRide(rideId, createStopRequest("Adresa", 2.0)));

        verifyNoInteractions(routeRepository);
        verifyNoInteractions(userRepository);
        verify(rideRepository, never()).save(any());
    }

    // helpers

    private Ride createActiveRide(Long rideId, Long driverId, Long creatorId, VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle();
        vehicle.setType(vehicleType);

        User driver = new User();
        driver.setId(driverId);
        driver.setVehicle(vehicle);
        driver.setLogoutPending(false);

        User creator = new User();
        creator.setId(creatorId);

        Route route = new Route();
        route.setId(200L);
        route.setStartAddress("Pocetna");

        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ACTIVE);
        ride.setDriver(driver);
        ride.setCreatedBy(creator);
        ride.setRoute(route);
        ride.setStartTime(LocalDateTime.now().minusMinutes(10));
        ride.setPassengers(new ArrayList<>());

        return ride;
    }

    private Pricing createPricing(VehicleType type, Integer start, Integer perKm) {
        Pricing p = new Pricing();
        p.setVehicleType(type);
        p.setStartingPrice(start);
        p.setPricePerKm(perKm);
        return p;
    }

    private StopRideRequestDTO createStopRequest(String addr, double dist) {
        StopRideRequestDTO dto = new StopRideRequestDTO();
        dto.setStopAddress(addr);
        dto.setDistanceTravelledKm(dist);
        dto.setVisitedStops(List.of("Stop A", "Stop B"));
        return dto;
    }
}