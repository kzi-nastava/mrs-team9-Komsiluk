package rs.ac.uns.ftn.iss.Komsiluk.s3.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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
import rs.ac.uns.ftn.iss.Komsiluk.services.DriverService;
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
    private PricingRepository pricingRepository;

    @Mock
    private DriverService driverService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private RideDTOMapper rideMapper;

    @InjectMocks
    private RideService rideService;

    @Captor
    private ArgumentCaptor<Ride> rideCaptor;

    @Captor
    private ArgumentCaptor<Route> routeCaptor;

    @Test
    @DisplayName("Should successfully stop active ride, calculate price and update driver status")
    void stopRide_Success() {
        //arrange
        Long rideId = 1L;
        Long driverId = 10L;
        Long passengerId = 55L;
        Long creatorId = 99L;
        VehicleType type = VehicleType.STANDARD;

        Ride ride = createActiveRide(rideId, driverId, creatorId, type);
        addPassengerToRide(ride, passengerId);

        Pricing pricing = createPricing(type, 120, 60);

        StopRideRequestDTO requestDTO = createStopRequest("Nova Adresa 123", 5.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(pricingRepository.findByVehicleType(type)).thenReturn(Optional.of(pricing));
        when(rideMapper.toResponseDTO(any(Ride.class))).thenReturn(new RideResponseDTO());

        //act
        rideService.stopRide(rideId, requestDTO);

        //assert
        verify(routeRepository).save(routeCaptor.capture());
        Route savedRoute = routeCaptor.getValue();
        assertEquals("Nova Adresa 123", savedRoute.getEndAddress());
        assertTrue(savedRoute.getStops().contains("Stop A|Stop B"));

        verify(rideRepository).save(rideCaptor.capture());
        Ride savedRide = rideCaptor.getValue();

        assertEquals(RideStatus.FINISHED, savedRide.getStatus());
        assertNotNull(savedRide.getEndTime());
        assertEquals(5.0, savedRide.getDistanceKm());

        assertEquals(0, BigDecimal.valueOf(420.0).compareTo(savedRide.getPrice()));

        verify(driverService).updateDriverStatus(eq(driverId), eq(DriverStatus.ACTIVE));

        //driver, creator, and passenger should all receive notifications
        verify(notificationService, times(3)).createNotification(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when ride does not exist")
    void stopRide_RideNotFound() {
        //arrange
        Long rideId = 999L;
        when(rideRepository.findById(rideId)).thenReturn(Optional.empty());

        // act and asert
        assertThrows(NotFoundException.class, () ->
                rideService.stopRide(rideId, createStopRequest("Adresa", 2.0))
        );
        verifyNoInteractions(routeRepository);
        verifyNoInteractions(pricingRepository);
        verifyNoInteractions(driverService);
        verify(rideRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw BadRequestException when ride is not ACTIVE")
    void stopRide_RideNotActive() {
        // arange
        Long rideId = 1L;
        Ride ride = createActiveRide(rideId, 10L, 99L, VehicleType.STANDARD);
        ride.setStatus(RideStatus.SCHEDULED);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));

        // act and assert
        assertThrows(BadRequestException.class, () ->
                rideService.stopRide(rideId, createStopRequest("Adresa", 2.0))
        );
        verifyNoInteractions(routeRepository);
        verifyNoInteractions(pricingRepository);
        verifyNoInteractions(driverService);
        verify(rideRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw NotFoundException when pricing is missing for vehicle type")
    void stopRide_PricingNotFound() {
        // arrange
        Long rideId = 1L;
        VehicleType type = VehicleType.VAN;

        Ride ride = createActiveRide(rideId, 10L, 99L, type);
        StopRideRequestDTO requestDTO = createStopRequest("Adresa", 5.0);

        when(rideRepository.findById(rideId)).thenReturn(Optional.of(ride));
        when(pricingRepository.findByVehicleType(type)).thenReturn(Optional.empty());

        // act  and assert
        assertThrows(NotFoundException.class, () ->
                rideService.stopRide(rideId, requestDTO)
        );
        verify(routeRepository).save(ride.getRoute());
        verify(pricingRepository).findByVehicleType(type);
        verify(rideRepository, never()).save(any());
        verifyNoInteractions(driverService);
    }

    // helpers

    private Ride createActiveRide(Long rideId, Long driverId, Long creatorId, VehicleType vehicleType) {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(100L);
        vehicle.setType(vehicleType);

        User driver = new User();
        driver.setId(driverId);
        driver.setFirstName("Marko");
        driver.setVehicle(vehicle);


        User creator = new User();
        creator.setId(creatorId);


        Route route = new Route();
        route.setId(200L);
        route.setStartAddress("Stara Adresa");
        route.setStops("");


        Ride ride = new Ride();
        ride.setId(rideId);
        ride.setStatus(RideStatus.ACTIVE);
        ride.setDriver(driver);
        ride.setCreatedBy(creator);
        ride.setRoute(route);
        ride.setStartTime(LocalDateTime.now().minusMinutes(15));
        ride.setPassengers(new ArrayList<>());

        return ride;
    }

    private void addPassengerToRide(Ride ride, Long passengerId) {
        User passenger = new User();
        passenger.setId(passengerId);
        ride.getPassengers().add(passenger);
    }

    private Pricing createPricing(VehicleType type, Integer startingPrice, Integer pricePerKm) {
        Pricing pricing = new Pricing();
        pricing.setVehicleType(type);
        pricing.setStartingPrice(startingPrice);
        pricing.setPricePerKm(pricePerKm);
        return pricing;
    }

    private StopRideRequestDTO createStopRequest(String stopAddress, double distanceKm) {
        StopRideRequestDTO dto = new StopRideRequestDTO();
        dto.setStopAddress(stopAddress);
        dto.setDistanceTravelledKm(distanceKm);
        dto.setVisitedStops(List.of("Stop A", "Stop B"));
        return dto;
    }
}