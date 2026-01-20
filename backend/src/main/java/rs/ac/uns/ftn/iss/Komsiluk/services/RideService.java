package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.*;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.*;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.*;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.*;
import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideLiveInfoDTO;


@Service
public class RideService implements IRideService {

	@Autowired
    private RideRepository rideRepository;
	@Autowired
    private IRouteService routeService;
	@Autowired
    private IUserService userService;
	@Autowired
    private INotificationService notificationService;
	@Autowired
    private RideDTOMapper rideMapper;
	@Autowired
	private IDriverService driverService;
	@Autowired
	private RouteDTOMapper routeMapper;
	@Autowired
	private IDriverActivityService driverActivityService;
    @Autowired
    private IDriverLocationService driverLocationService;
    @Autowired
    private IRatingService ratingService;
    @Autowired
    private IInconsistencyReportService inconsistencyReportService;
    @Autowired
    private DriverDTOMapper driverMapper;
    @Autowired
    private AdminRideDetailsMapper adminRideDetailsMapper;
    @Autowired
    private AdminRideHistoryMapper adminRideHistoryMapper;

    @Override
    public RideResponseDTO orderRide(RideCreateDTO dto) {

        User creator = userService.findById(dto.getCreatorId());
        
        if (userHasActiveRide(creator.getId())) {
            throw new BadRequestException();
        }

        String stopsString = String.join("|",dto.getStops() == null ? List.<String>of() : dto.getStops());
        
        RouteCreateDTO routeCreateDTO = new RouteCreateDTO();
        routeCreateDTO.setStartAddress(dto.getStartAddress());
        routeCreateDTO.setEndAddress(dto.getEndAddress());
        routeCreateDTO.setStops(stopsString);
        routeCreateDTO.setDistanceKm(dto.getDistanceKm());
        routeCreateDTO.setEstimatedDurationMin(dto.getEstimatedDurationMin());

        RouteResponseDTO routeDTO = routeService.findOrCreate(routeCreateDTO);
        
        Route route = routeMapper.fromResponseDTO(routeDTO);

        BigDecimal price = calculatePrice(dto.getVehicleType(), dto.getDistanceKm());
		
        List<User> passengers = new ArrayList<>();

        if (dto.getPassengerEmails() != null) {
            for (String email : dto.getPassengerEmails()) {
                User passenger= userService.findByEmail(email);
                if (passenger == null) {
            	  throw new NotFoundException();
            	}
                passengers.add(passenger);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime scheduledAt = dto.getScheduledAt();

        boolean scheduled = scheduledAt != null;

        Optional<DriverResponseDTO> maybeDriver = findBestDriver(dto.getVehicleType(), dto.isBabyFriendly(), dto.isPetFriendly(), scheduled, scheduledAt);

        Ride ride = new Ride();
        ride.setCreatedAt(now);
        ride.setScheduledAt(scheduledAt);
        ride.setRoute(route);
        ride.setPassengers(passengers);
        ride.setPrice(price);
        ride.setCreatedBy(creator);
        ride.setVehicleType(dto.getVehicleType());
        ride.setBabyFriendly(dto.isBabyFriendly());
        ride.setPetFriendly(dto.isPetFriendly());
        ride.setDistanceKm(dto.getDistanceKm());
        ride.setEstimatedDurationMin(dto.getEstimatedDurationMin());

        if (maybeDriver.isEmpty()) { 
            ride.setStatus(RideStatus.REJECTED);
            ride = rideRepository.save(ride);

            NotificationCreateDTO notificationDTOFail = new NotificationCreateDTO();
			notificationDTOFail.setUserId(creator.getId());
			notificationDTOFail.setType(NotificationType.RIDE_FAILED);
			notificationDTOFail.setTitle("Ride Failed");
			notificationDTOFail.setMessage("There are currently no available drivers.");
            notificationService.createNotification(notificationDTOFail);

            return rideMapper.toResponseDTO(ride);
        }

        User driver = userService.findById(maybeDriver.get().getId());
        ride.setDriver(driver);
        ride.setStatus(scheduled ? RideStatus.SCHEDULED : RideStatus.ASSIGNED);

        ride = rideRepository.save(ride);

        NotificationCreateDTO notificationDTODriver = new NotificationCreateDTO();
        notificationDTODriver.setUserId(driver.getId());
        notificationDTODriver.setType(NotificationType.RIDE_ASSIGNED);
        notificationDTODriver.setTitle("New Ride Assigned");
        notificationDTODriver.setMessage("You have been assigned a new ride from " + dto.getStartAddress() + " to " + dto.getEndAddress());
        notificationService.createNotification(notificationDTODriver);

        NotificationCreateDTO notificationDTOCreator = new NotificationCreateDTO();
        notificationDTOCreator.setUserId(creator.getId());
        notificationDTOCreator.setType(NotificationType.RIDE_ASSIGNED);
        notificationDTOCreator.setTitle("Ride Accepted");
        notificationDTOCreator.setMessage("Your ride has been successfully ordered.");
        notificationService.createNotification(notificationDTOCreator);

        for (int i = 1; i < passengers.size(); i++) {
            User p = passengers.get(i);
            
            NotificationCreateDTO notificationDTOPassenger = new NotificationCreateDTO();
            notificationDTOPassenger.setUserId(p.getId());
            notificationDTOPassenger.setType(NotificationType.INFO);
            notificationDTOPassenger.setTitle("Added to Ride");
            notificationDTOPassenger.setMessage("You have been added as a passenger to a ride from " + dto.getStartAddress() + " to " + dto.getEndAddress());
            notificationService.createNotification(notificationDTOPassenger);
        }

        return rideMapper.toResponseDTO(ride);
    }
    
    @Override
    public Collection<RideResponseDTO> getScheduledRidesForUser(Long userId) {
        if (userService.findById(userId) == null) {
            throw new NotFoundException();
        }

        Collection<Ride> rides = rideRepository.findScheduledByUserId(userId,RideStatus.SCHEDULED);

        return rides.stream().map(rideMapper::toResponseDTO).collect(Collectors.toList());
    }
    
    @Override
    public RideResponseDTO startRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);
        if (ride == null) {
            throw new NotFoundException();
        }

        if (ride.getStatus() != RideStatus.ASSIGNED && ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException();
        }

        ride.setStatus(RideStatus.ACTIVE);
        ride.setStartTime(LocalDateTime.now());

        rideRepository.save(ride);

        return rideMapper.toResponseDTO(ride);
    }

    @Override
    public RideResponseDTO finishRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);
        if (ride == null) {
            throw new NotFoundException();
        }

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException();
        }

        ride.setStatus(RideStatus.FINISHED);
        ride.setEndTime(LocalDateTime.now());

        rideRepository.save(ride);

        return rideMapper.toResponseDTO(ride);
    }

    @Override
    public RideLiveInfoDTO getLiveInfo(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException();
        }
        if (ride.getDriver() == null) {
            throw new BadRequestException();
        }

        RideLiveInfoDTO dto = new RideLiveInfoDTO();
        dto.setRideId(ride.getId());
        dto.setStatus(ride.getStatus());
        dto.setDriverId(ride.getDriver().getId());

        DriverLocation loc = driverLocationService.getLiveLocation(dto.getDriverId());
        if (loc != null) {
            dto.setLat(loc.getLat());
            dto.setLng(loc.getLng());
            dto.setLocationUpdatedAt(loc.getUpdatedAt());
        }

        Integer est = (ride.getRoute() != null) ? ride.getRoute().getEstimatedDurationMin() : null;
        LocalDateTime start = ride.getStartTime();

        if (est != null) {
            if (start == null) {
                dto.setRemainingMinutes(est);
            } else {
                long elapsed = Duration.between(start, LocalDateTime.now()).toMinutes();
                int remaining = Math.max(0, est - (int) elapsed);
                dto.setRemainingMinutes(remaining);
            }
        }

        return dto;
    }

    @Override
    public Collection<RideResponseDTO> getDriverRideHistory(Long driverId, LocalDate from, LocalDate to) {

        return rideRepository.findAll().stream()
                .filter(r -> r.getDriver() != null && r.getDriver().getId() != null
                        && r.getDriver().getId().equals(driverId))
                .filter(r -> {
                    if (from == null && to == null) return true;
                    if (r.getCreatedAt() == null) return false;

                    LocalDate created = r.getCreatedAt().toLocalDate();
                    if (from != null && created.isBefore(from)) return false;
                    if (to != null && created.isAfter(to)) return false;
                    return true;
                })
                .sorted(Comparator.comparing(Ride::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(rideMapper::toResponseDTO)
                .collect(Collectors.toList());
    }



    @Override
    public boolean userHasActiveRide(Long userId) {

        return rideRepository.findAll().stream().filter(r -> r.getStatus() == RideStatus.ACTIVE).anyMatch(r -> isPassenger(r, userId));
    }

    private boolean isPassenger(Ride ride, Long userId) {
        return ride.getPassengers() != null && ride.getPassengers().stream().anyMatch(u -> u.getId().equals(userId));
    }

    private BigDecimal calculatePrice(VehicleType type, double distanceKm) {
        // Here should price logic go
        BigDecimal base = switch (type) {
            case STANDARD -> BigDecimal.valueOf(200);
            case LUXURY -> BigDecimal.valueOf(300);
            case VAN -> BigDecimal.valueOf(400);
        };

        BigDecimal perKm = BigDecimal.valueOf(120);
        return base.add(perKm.multiply(BigDecimal.valueOf(distanceKm)));
    }

 
    private Optional<DriverResponseDTO> findBestDriver(VehicleType vt, boolean babyFriendly, boolean petFriendly, boolean scheduled, LocalDateTime scheduledAt) {

        Collection<DriverResponseDTO> drivers = driverService.getAllDrivers();

        // this is a simplified version, better logic should be implemented
        return drivers.stream()
                .filter(d -> d.getDriverStatus() == DriverStatus.ACTIVE)
                .filter(d -> { return driverActivityService.canAcceptNewRide(d.getId());})
                .filter(d -> !d.isBlocked())
                .filter(d -> d.getVehicle() != null)
                .filter(d -> d.getVehicle().getType() == vt)
                .filter(d -> !babyFriendly || d.getVehicle().isBabyFriendly())
                .filter(d -> !petFriendly || d.getVehicle().isPetFriendly())
                .findFirst();
    }

    public void cancelByDriver(Long rideId, DriverCancelRideDTO dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.ASSIGNED &&
                ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException();
        }

        if (ride.getScheduledAt() == null) {
            throw new BadRequestException();
        }


        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(dto.getReason());
        ride.setCancellationSource(CancellationSource.DRIVER);

        rideRepository.save(ride);

        notifyRideParticipants(ride, NotificationType.RIDE_CANCELLED);
    }

    public void cancelByPassenger(Long rideId, PassengerCancelRideDTO dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.ASSIGNED &&
                ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException();
        }

        if (ride.getScheduledAt() == null) {
            throw new BadRequestException();
        }

        if (LocalDateTime.now().isAfter(
                ride.getScheduledAt().minusMinutes(10))) {
            throw new BadRequestException();
        }


        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(dto.getReason());
        ride.setCancellationSource(CancellationSource.PASSENGER);

        rideRepository.save(ride);

        notifyRideParticipants(ride, NotificationType.RIDE_CANCELLED);
    }



    public StopRideResponseDTO stopRide(Long rideId, StopRideRequestDTO dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException();
        }


        LocalDateTime endTime = LocalDateTime.now();
        ride.setEndTime(endTime);

        long durationMinutes = java.time.Duration.between(ride.getStartTime(), endTime).toMinutes();

        Route route = ride.getRoute();
        route.setEndAddress(dto.getStopAddress());
        route.setStops(dto.getVisitedStops());
        route.setDistanceKm(dto.getDistanceTravelledKm());
        route.setEstimatedDurationMin((int) durationMinutes);
        ride.setRoute(route);

        ride.setStatus(RideStatus.FINISHED);

        BigDecimal price = calculatePrice(ride.getDriver().getVehicle().getType(), dto.getDistanceTravelledKm());

        ride.setPrice(price);

        rideRepository.save(ride);

        StopRideResponseDTO response = new StopRideResponseDTO();
        response.setFinalAddress(dto.getStopAddress());
        response.setDurationMinutes(ride.getRoute().getEstimatedDurationMin());
        response.setPrice(price.doubleValue());


        notifyRideParticipants(ride, NotificationType.RIDE_STOPPED);
        return response;
    }

    public void handlePanicButton(Long rideId, PanicRequestDTO initiator) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(NotFoundException::new);

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException();
        }

        ride.setPanicTriggered(true);

        rideRepository.save(ride);

        NotificationCreateDTO notificationDTOCreator = new NotificationCreateDTO();
        notificationDTOCreator.setUserId(initiator.getInitiatorId());
        notificationDTOCreator.setType(NotificationType.PANIC);
        notificationDTOCreator.setTitle("PANIC activated");
        notificationDTOCreator.setMessage("You have activated the panic button.");
        notificationService.createNotification(notificationDTOCreator);
    }

    private void notifyRideParticipants(Ride ride, NotificationType type) {
        String title = "";
        String message = "";
        switch(type) {
            case RIDE_CANCELLED:
                title = "Ride Cancelled";
                message = "Your ride from " + ride.getRoute().getStartAddress() +
                        "to " + ride.getRoute().getEndAddress() + " has been cancelled.";
                break;
            case RIDE_STOPPED:
                title = "Ride Stopped";
                message = "Your ride from has stopped at " + ride.getRoute().getEndAddress() + ".";
                break;
            case PANIC:
                title = "Panic Button Pressed";
                message = "PANIC button pressed!";
                break;
            default:
                throw new BadRequestException();
        }

        NotificationCreateDTO notificationDTOCreator = new NotificationCreateDTO();
        notificationDTOCreator.setUserId(ride.getCreatedBy().getId());
        notificationDTOCreator.setType(type);
        notificationDTOCreator.setTitle(title);
        notificationDTOCreator.setMessage(message);
        notificationService.createNotification(notificationDTOCreator);

        List<User> passengers = ride.getPassengers();

        for (User passenger : passengers) {
            NotificationCreateDTO notificationDTOPassenger = new NotificationCreateDTO();
            notificationDTOPassenger.setUserId(passenger.getId());
            notificationDTOCreator.setType(type);
            notificationDTOCreator.setTitle(title);
            notificationDTOCreator.setMessage(message);
            notificationService.createNotification(notificationDTOPassenger);
        }
    }

//    public Collection<AdminRideHistoryDTO> getAdminRideHistory(
//            LocalDate from,
//            LocalDate to,
//            String sortBy
//    ) {
//
//        return rideRepository.findAll().stream()
//                .filter(r ->
//                        r.getStatus() == RideStatus.FINISHED ||
//                                r.getStatus() == RideStatus.CANCELLED
//                )
//                .filter(r -> {
//                    if (from != null && r.getCreatedAt().toLocalDate().isBefore(from)) {
//                        return false;
//                    }
//                    if (to != null && r.getCreatedAt().toLocalDate().isAfter(to)) {
//                        return false;
//                    }
//                    return true;
//                })
//                .sorted(getAdminSortComparator(sortBy))
//                .map(adminRideHistoryMapper::toDto)
//                .toList();
//    }

    public AdminRideDetailsDTO getAdminRideDetails(Long rideId) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(NotFoundException::new);

        var ratings = ratingService.getRatingsForRide(rideId);
        var reports = inconsistencyReportService.getByRideId(rideId);

        return adminRideDetailsMapper.toDto(ride, ratings, reports);
    }


    public Collection<AdminRideHistoryDTO> getAdminRideHistoryForUser(
            Long userId,
            LocalDate from,
            LocalDate to,
            AdminRideSortBy sortBy
    ) {
        var statuses = List.of(RideStatus.FINISHED, RideStatus.CANCELLED);

        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : LocalDateTime.MIN;
        LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : LocalDateTime.MAX;

        return rideRepository
                .findAdminRideHistoryForUser(userId, statuses, fromDateTime, toDateTime)
                .stream()
                .sorted(getAdminSortComparator(sortBy))
                .map(adminRideHistoryMapper::toDto)
                .toList();
    }



//    private boolean isUserOnRide(Ride ride, Long userId) {
//        // ako je vozač
//        if (ride.getDriver() != null &&
//                ride.getDriver().getId().equals(userId)) {
//            return true;
//        }
//
//        // ako je putnik
//        if (ride.getPassengers() != null) {
//            return ride.getPassengers().stream()
//                    .anyMatch(p -> p != null && p.getId().equals(userId));
//        }
//
//        return false;
//    }




    private Comparator<Ride> getAdminSortComparator(AdminRideSortBy sortBy) {
        if (sortBy == null) {
            return Comparator.comparing(Ride::getCreatedAt).reversed();
        }

        return switch (sortBy) {
            case DATE ->
                    Comparator.comparing(Ride::getCreatedAt).reversed();

            case PRICE ->
                    Comparator.comparing(Ride::getPrice,
                            Comparator.nullsLast(BigDecimal::compareTo));

            case START_TIME ->
                    Comparator.comparing(Ride::getStartTime,
                            Comparator.nullsLast(LocalDateTime::compareTo));

            case END_TIME ->
                    Comparator.comparing(Ride::getEndTime,
                            Comparator.nullsLast(LocalDateTime::compareTo));

            case START_ADDRESS ->
                    Comparator.comparing(r -> r.getRoute().getStartAddress(),
                            Comparator.nullsLast(String::compareToIgnoreCase));

            case END_ADDRESS ->
                    Comparator.comparing(r -> r.getRoute().getEndAddress(),
                            Comparator.nullsLast(String::compareToIgnoreCase));

            case ROUTE ->
                    Comparator.comparing(this::buildRouteString,
                            String.CASE_INSENSITIVE_ORDER);

            case CANCELLED ->
                    Comparator.comparing(r -> r.getStatus() == RideStatus.CANCELLED);

            case CANCELLED_BY ->
                    Comparator.comparing(Ride::getCancellationSource,
                            Comparator.nullsLast(Enum::compareTo));

            case PANIC ->
                    Comparator.comparing(Ride::isPanicTriggered);
        };
    }

    private String buildRouteString(Ride ride) {
        Route r = ride.getRoute();
        return String.join(" | ",
                r.getStartAddress(),
                r.getStops() == null ? "" : r.getStops(),
                r.getEndAddress()
        );
    }



}
