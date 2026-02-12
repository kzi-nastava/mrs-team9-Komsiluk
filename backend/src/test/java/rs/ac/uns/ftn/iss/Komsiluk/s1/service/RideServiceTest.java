package rs.ac.uns.ftn.iss.Komsiluk.s1.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Pricing;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RideDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RouteDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.PricingRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.DriverService;
import rs.ac.uns.ftn.iss.Komsiluk.services.MailService;
import rs.ac.uns.ftn.iss.Komsiluk.services.RideService;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverActivityService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverLocationService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IInconsistencyReportService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.INotificationService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRatingService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRouteService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private IRouteService routeService;
    @Mock
    private IUserService userService;
    @Mock
    private INotificationService notificationService;
    @Mock
    private RouteDTOMapper routeMapper;
    @Mock
    private RideDTOMapper rideMapper;
    @Mock
    private PricingRepository pricingRepository;
    @Mock
    private IDriverActivityService driverActivityService;
    @Mock
    private IDriverLocationService driverLocationService;
    @Mock
    private MailService mailService;

    // nullable dependencies that are not used in these tests
    @Mock
    private IRatingService ratingService;
    @Mock
    private IInconsistencyReportService inconsistencyReportService;
    @Mock
    private DriverService driverService;

    @Captor private ArgumentCaptor<NotificationCreateDTO> notificationCaptor;
    @Captor private ArgumentCaptor<Ride> rideCaptor;

    @InjectMocks
    private RideService rideService;

    private static final LocalDateTime BASE = LocalDateTime.of(2030, 1, 1, 12, 0);

    @Test
    void orderRide_throwsBadRequest_andSendsRideFailedNotification_whenConflictExists() {
    	
    	//setup
        User creator = creator(11L, "creator@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(true);
        
        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(BASE.plusHours(1));

        //assert
        assertThrows(BadRequestException.class, () -> rideService.orderRide(dto));

        verify(notificationService).createNotification(notificationCaptor.capture());
        NotificationCreateDTO sent = notificationCaptor.getValue();
        assertEquals(creator.getId(), sent.getUserId());
        assertEquals(NotificationType.RIDE_FAILED, sent.getType());

        verifyNoMoreInteractions(notificationService);
        verifyNoInteractions(routeService, routeMapper, pricingRepository, userRepository, mailService, driverActivityService, driverLocationService);
    }

    @Test
    void orderRide_savesRejectedRide_andNotifiesCreator_whenNoDriverCandidates_forScheduledRide() {
    	
    	//setup
        User creator = creator(21L, "creator2@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);
        
        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(BASE.plusHours(2));
        dto.setEstimatedDurationMin(20);
        dto.setDistanceKm(5.0);

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(100L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        Route route = new Route();
        route.setId(100L);
        route.setStartAddress(dto.getStartAddress());
        route.setEndAddress(dto.getEndAddress());
        route.setStops("");
        route.setDistanceKm(dto.getDistanceKm());
        route.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(route);

        Pricing pricing = new Pricing();
        pricing.setVehicleType(dto.getVehicleType());
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(50);
        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.of(pricing));

        when(userRepository.findAvailableDriversNoConflict(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of());
        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RideResponseDTO mapped = mock(RideResponseDTO.class);
        when(rideMapper.toResponseDTO(any())).thenReturn(mapped);

        //assert
        RideResponseDTO result = rideService.orderRide(dto);
        assertNotNull(result);

        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();

        assertEquals(RideStatus.REJECTED, saved.getStatus());
        assertEquals(dto.getScheduledAt(), saved.getScheduledAt());
        assertEquals(dto.getScheduledAt(), saved.getStartTime());
        assertEquals(dto.getScheduledAt().plusMinutes(dto.getEstimatedDurationMin() + 10), saved.getEndTime());
        assertNull(saved.getDriver());

        verify(notificationService).createNotification(notificationCaptor.capture());
        assertEquals(NotificationType.RIDE_FAILED, notificationCaptor.getValue().getType());

        verify(mailService, never()).sendAddedToRideMail(anyString());
        verifyNoMoreInteractions(notificationService);
        verifyNoInteractions(driverActivityService, driverLocationService);
    }

    @Test
    void orderRide_schedulesRide_withDriverHavingLeastScheduledCount_whenScheduledRide() {
    	
    	//setup
        User creator = creator(31L, "creator3@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);

        User driverA = driver(101L, "driverA@test.com");
        User driverB = driver(102L, "driverB@test.com");

        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(BASE.plusHours(3));
        dto.setEstimatedDurationMin(15);
        dto.setDistanceKm(2.0);

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(200L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        Route route = new Route();
        route.setId(200L);
        route.setStartAddress(dto.getStartAddress());
        route.setEndAddress(dto.getEndAddress());
        route.setStops("");
        route.setDistanceKm(dto.getDistanceKm());
        route.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(route);

        Pricing pricing = new Pricing();
        pricing.setVehicleType(dto.getVehicleType());
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(10);
        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.of(pricing));

        when(userRepository.findAvailableDriversNoConflict(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of(driverA, driverB));
        
        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverA), any())).thenReturn(0L);
        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverB), any())).thenReturn(0L);

        when(rideRepository.countScheduledForDriverFrom(eq(driverA.getId()), any())).thenReturn(5L);
        when(rideRepository.countScheduledForDriverFrom(eq(driverB.getId()), any())).thenReturn(2L);

        when(userService.findById(driverB.getId())).thenReturn(driverB);

        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(mock(RideResponseDTO.class));

        //assert
        rideService.orderRide(dto);

        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();

        assertEquals(RideStatus.SCHEDULED, saved.getStatus());
        assertNotNull(saved.getDriver());
        assertEquals(driverB.getId(), saved.getDriver().getId());

        verify(notificationService, atLeast(2)).createNotification(notificationCaptor.capture());
        List<NotificationCreateDTO> all = notificationCaptor.getAllValues();

        assertTrue(all.stream().anyMatch(n -> n.getUserId().equals(driverB.getId()) && n.getType() == NotificationType.RIDE_ASSIGNED));
        assertTrue(all.stream().anyMatch(n -> n.getUserId().equals(creator.getId()) && n.getType() == NotificationType.RIDE_ASSIGNED));
    }
    
    @Test
    void orderRide_savesRejectedRide_andNotifiesCreator_whenNoCandidates_andNoFinishingSoon_forImmediateRide() {

    	//setup
        User creator = creator(41L, "creator4@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);

        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(null);
        dto.setEstimatedDurationMin(20);
        dto.setDistanceKm(5.0);

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(300L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        Route route = new Route();
        route.setId(300L);
        route.setStartAddress(dto.getStartAddress());
        route.setEndAddress(dto.getEndAddress());
        route.setStops("");
        route.setDistanceKm(dto.getDistanceKm());
        route.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(route);

        Pricing pricing = new Pricing();
        pricing.setVehicleType(dto.getVehicleType());
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(10);
        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.of(pricing));

        when(userRepository.findAvailableDriversNoConflict(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of());
        when(userRepository.findDriversFinishingSoon(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of());

        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(mock(RideResponseDTO.class));

        //assert
        rideService.orderRide(dto);

        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        assertEquals(RideStatus.REJECTED, saved.getStatus());
        assertNull(saved.getDriver());
        assertNotNull(saved.getCreatedAt());

        verify(notificationService).createNotification(notificationCaptor.capture());
        NotificationCreateDTO sent = notificationCaptor.getValue();
        assertEquals(creator.getId(), sent.getUserId());
        assertEquals(NotificationType.RIDE_FAILED, sent.getType());

        verify(mailService, never()).sendAddedToRideMail(anyString());
        verifyNoInteractions(driverActivityService, driverLocationService);
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void orderRide_assignsNearestDriver_whenImmediateRide_andCandidatesComeFromFinishingSoon() {

    	//setup
        User creator = creator(51L, "creator5@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);

        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(null);
        dto.setEstimatedDurationMin(10);
        dto.setDistanceKm(2.0);
        dto.setStartLat(45.0);
        dto.setStartLng(19.0);

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(400L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        Route route = new Route();
        route.setId(400L);
        route.setStartAddress(dto.getStartAddress());
        route.setEndAddress(dto.getEndAddress());
        route.setStops("");
        route.setDistanceKm(dto.getDistanceKm());
        route.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(route);

        Pricing pricing = new Pricing();
        pricing.setVehicleType(dto.getVehicleType());
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(10);
        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.of(pricing));

        User driverNear = driver(201L, "near@test.com");
        User driverFar  = driver(202L, "far@test.com");

        when(userRepository.findAvailableDriversNoConflict(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of());
        when(userRepository.findDriversFinishingSoon(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of(driverNear, driverFar));

        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverNear), any())).thenReturn(0L);
        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverFar), any())).thenReturn(0L);

        DriverLocation locNear = new DriverLocation();
        locNear.setLat(45.0);
        locNear.setLng(19.0);
        DriverLocation locFar = new DriverLocation();
        locFar.setLat(46.0);
        locFar.setLng(20.0);
        when(driverLocationService.getLiveLocation(driverNear.getId())).thenReturn(locNear);
        when(driverLocationService.getLiveLocation(driverFar.getId())).thenReturn(locFar);

        when(userService.findById(driverNear.getId())).thenReturn(driverNear);

        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(mock(RideResponseDTO.class));

        //assert
        rideService.orderRide(dto);

        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        assertEquals(RideStatus.ASSIGNED, saved.getStatus());
        assertNotNull(saved.getDriver());
        assertEquals(driverNear.getId(), saved.getDriver().getId());

        verify(notificationService, atLeast(2)).createNotification(notificationCaptor.capture());
        List<NotificationCreateDTO> all = notificationCaptor.getAllValues();
        assertTrue(all.stream().anyMatch(n -> n.getUserId().equals(driverNear.getId()) && n.getType() == NotificationType.RIDE_ASSIGNED));
        assertTrue(all.stream().anyMatch(n -> n.getUserId().equals(creator.getId()) && n.getType() == NotificationType.RIDE_ASSIGNED));

        verifyNoInteractions(mailService);
    }

    @Test
    void orderRide_throwsNotFound_whenPricingMissing_andDoesNotSaveRide() {

    	//setup
        User creator = creator(61L, "creator6@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);

        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(BASE.plusHours(4));
        dto.setEstimatedDurationMin(10);

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(500L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(new Route());

        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.empty());

        //assert
        assertThrows(NotFoundException.class, () -> rideService.orderRide(dto));

        verify(rideRepository, never()).save(any());
        verifyNoInteractions(notificationService, mailService, userRepository, driverActivityService, driverLocationService);
    }

    @Test
    void orderRide_sendsPassengerNotificationsAndMails_whenPassengersProvided_andRideAssigned() {

    	//setup
        User creator = creator(71L, "creator7@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);

        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(null);
        dto.setPassengerEmails(List.of("p1@test.com", "p2@test.com"));

        User p1 = creator(801L, "p1@test.com");
        User p2 = creator(802L, "p2@test.com");
        when(userService.findByEmail("p1@test.com")).thenReturn(p1);
        when(userService.findByEmail("p2@test.com")).thenReturn(p2);

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(600L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        Route route = new Route();
        route.setId(600L);
        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(route);

        Pricing pricing = new Pricing();
        pricing.setVehicleType(dto.getVehicleType());
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(10);
        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.of(pricing));

        User driver = driver(901L, "driver@test.com");

        when(userRepository.findAvailableDriversNoConflict(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of(driver));

        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driver), any())).thenReturn(0L);

        DriverLocation loc = new DriverLocation();
        loc.setLat(45.0);
        loc.setLng(19.0);
        when(driverLocationService.getLiveLocation(driver.getId())).thenReturn(loc);

        when(userService.findById(driver.getId())).thenReturn(driver);

        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(mock(RideResponseDTO.class));

        //assert
        rideService.orderRide(dto);

        verify(mailService).sendAddedToRideMail(eq("p1@test.com"));
        verify(mailService).sendAddedToRideMail(eq("p2@test.com"));

        verify(notificationService, atLeast(1)).createNotification(notificationCaptor.capture());
        List<NotificationCreateDTO> all = notificationCaptor.getAllValues();

        assertTrue(all.stream().anyMatch(n -> n.getUserId().equals(p1.getId()) && n.getType() == NotificationType.INFO));
        assertTrue(all.stream().anyMatch(n -> n.getUserId().equals(p2.getId()) && n.getType() == NotificationType.INFO));
    }

    @Test
    void orderRide_savesRejectedRide_whenImmediateRide_andAllCandidatesHaveNoLiveLocation() {

    	//setup
        User creator = creator(91L, "creator91@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);

        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(null);

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(910L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(new Route());

        Pricing pricing = new Pricing();
        pricing.setVehicleType(dto.getVehicleType());
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(10);
        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.of(pricing));

        User driverA = driver(911L, "dla@test.com");
        User driverB = driver(912L, "dlb@test.com");

        when(userRepository.findAvailableDriversNoConflict(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of(driverA, driverB));

        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverA), any())).thenReturn(0L);
        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverB), any())).thenReturn(0L);

        when(driverLocationService.getLiveLocation(driverA.getId())).thenReturn(null);
        when(driverLocationService.getLiveLocation(driverB.getId())).thenReturn(null);

        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(mock(RideResponseDTO.class));

        //assert
        rideService.orderRide(dto);

        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        assertEquals(RideStatus.REJECTED, saved.getStatus());
        assertNull(saved.getDriver());

        verify(driverLocationService).getLiveLocation(driverA.getId());
        verify(driverLocationService).getLiveLocation(driverB.getId());

        verify(notificationService).createNotification(notificationCaptor.capture());
        NotificationCreateDTO sent = notificationCaptor.getValue();
        assertEquals(creator.getId(), sent.getUserId());
        assertEquals(NotificationType.RIDE_FAILED, sent.getType());

        verify(mailService, never()).sendAddedToRideMail(anyString());
        verify(rideRepository, never()).countScheduledForDriverFrom(anyLong(), any());
        verifyNoMoreInteractions(notificationService);
    }

    @Test
    void orderRide_savesRejectedRide_whenScheduledRide_andAllCandidatesOverMaxWorkedMinutesLast24h() {

    	//setup
        User creator = creator(101L, "creator101@test.com");
        when(userService.findById(creator.getId())).thenReturn(creator);
        when(rideRepository.existsBlockingRideForCreator(eq(creator.getId()), anyList(), any(), any())).thenReturn(false);

        RideCreateDTO dto = baseDto(creator.getId());
        dto.setScheduledAt(BASE.plusHours(6));

        RouteResponseDTO routeResp = new RouteResponseDTO();
        routeResp.setId(1010L);
        routeResp.setStartAddress(dto.getStartAddress());
        routeResp.setEndAddress(dto.getEndAddress());
        routeResp.setStops("");
        routeResp.setDistanceKm(dto.getDistanceKm());
        routeResp.setEstimatedDurationMin(dto.getEstimatedDurationMin());
        when(routeService.findOrCreate(any(RouteCreateDTO.class))).thenReturn(routeResp);

        when(routeMapper.fromResponseDTO(routeResp)).thenReturn(new Route());

        Pricing pricing = new Pricing();
        pricing.setVehicleType(dto.getVehicleType());
        pricing.setStartingPrice(100);
        pricing.setPricePerKm(10);
        when(pricingRepository.findByVehicleType(dto.getVehicleType())).thenReturn(Optional.of(pricing));

        User driverA = driver(1011L, "overA@test.com");
        User driverB = driver(1012L, "overB@test.com");

        when(userRepository.findAvailableDriversNoConflict(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any())).thenReturn(List.of(driverA, driverB));

        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverA), any())).thenReturn(480L);
        when(driverActivityService.getWorkedMinutesLast24hAt(eq(driverB), any())).thenReturn(999L);

        when(rideRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(rideMapper.toResponseDTO(any())).thenReturn(mock(RideResponseDTO.class));

        //assert
        rideService.orderRide(dto);

        verify(rideRepository).save(rideCaptor.capture());
        Ride saved = rideCaptor.getValue();
        assertEquals(RideStatus.REJECTED, saved.getStatus());
        assertNull(saved.getDriver());
        assertEquals(dto.getScheduledAt(), saved.getScheduledAt());

        verify(notificationService).createNotification(notificationCaptor.capture());
        NotificationCreateDTO sent = notificationCaptor.getValue();
        assertEquals(creator.getId(), sent.getUserId());
        assertEquals(NotificationType.RIDE_FAILED, sent.getType());

        verify(userRepository, never()).findDriversFinishingSoon(anyString(), anyInt(), anyBoolean(), anyBoolean(), any(), any());
        verify(rideRepository, never()).countScheduledForDriverFrom(anyLong(), any());
        verifyNoInteractions(driverLocationService);
        verifyNoMoreInteractions(notificationService);
    }

    
    
    // ---------- helpers ----------

    private RideCreateDTO baseDto(Long creatorId) {
        RideCreateDTO dto = new RideCreateDTO();
        dto.setCreatorId(creatorId);
        dto.setStartAddress("Start Address 1");
        dto.setEndAddress("End Address 1");
        dto.setStops(null);
        dto.setDistanceKm(1.0);
        dto.setEstimatedDurationMin(10);
        dto.setStartLat(45.0);
        dto.setStartLng(19.0);
        dto.setVehicleType(VehicleType.STANDARD);
        dto.setBabyFriendly(false);
        dto.setPetFriendly(false);
        dto.setPassengerEmails(null);
        return dto;
    }

    private User creator(Long id, String email) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setFirstName("Test");
        u.setLastName("Creator");
        u.setRole(UserRole.PASSENGER);
        u.setActive(true);
        u.setBlocked(false);
        return u;
    }

    private User driver(Long id, String email) {
        User u = new User();
        u.setId(id);
        u.setEmail(email);
        u.setFirstName("Test");
        u.setLastName("Driver");
        u.setRole(UserRole.DRIVER);
        u.setActive(true);
        u.setBlocked(false);
        return u;
    }
}
