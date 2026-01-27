package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.iss.Komsiluk.beans.*;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.*;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.*;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.*;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.PricingRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.*;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideLiveInfoDTO;

@Service
public class RideService implements IRideService {

	@Autowired
    private RideRepository rideRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
    private IRouteService routeService;
	@Autowired
    private IUserService userService;
	@Autowired
    private INotificationService notificationService;
	@Autowired
    private RideDTOMapper rideMapper;
	@Autowired
	private RouteDTOMapper routeMapper;
    @Autowired
    private IDriverLocationService driverLocationService;
    @Autowired
    private IRatingService ratingService;
    @Autowired
    private IInconsistencyReportService inconsistencyReportService;
    @Autowired
    private AdminRideDetailsMapper adminRideDetailsMapper;
    @Autowired
    private AdminRideHistoryMapper adminRideHistoryMapper;
    @Autowired
    private IDriverActivityService driverActivityService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PricingRepository pricingRepository;
    @Autowired
    private RideDTOMapper  rideDTOMapper;
    
    private static final long MAX_MINUTES_LAST_24H = 480;

    @Override
    public RideResponseDTO orderRide(RideCreateDTO dto) {

        User creator = userService.findById(dto.getCreatorId());
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime newStart = (dto.getScheduledAt() != null) ? dto.getScheduledAt() : now;

        int bufferMinutes = 10;
        LocalDateTime newEnd = newStart.plusMinutes(dto.getEstimatedDurationMin()).plusMinutes(bufferMinutes);

        boolean hasConflict = rideRepository.existsBlockingRideForCreator(creator.getId(), List.of("SCHEDULED", "ASSIGNED", "ACTIVE"), newStart, newEnd, bufferMinutes);

        if (hasConflict) {
            NotificationCreateDTO notificationDTOFail = new NotificationCreateDTO();
            notificationDTOFail.setUserId(creator.getId());
            notificationDTOFail.setType(NotificationType.RIDE_FAILED);
            notificationDTOFail.setTitle("Ride Failed");
            notificationDTOFail.setMessage("You already have a ride scheduled/active in that time window.");
            notificationService.createNotification(notificationDTOFail);

            throw new BadRequestException("You already have a ride scheduled/active in that time window.");
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
        List<User> linkedPassengers = new ArrayList<>();

        if (dto.getPassengerEmails() != null) {
            for (String email : dto.getPassengerEmails()) {
                User passenger= userService.findByEmail(email);
                if (passenger != null) {
                    passengers.add(passenger);
                    linkedPassengers.add(passenger);
				}
                
                //send email
            }
        }

        LocalDateTime scheduledAt = dto.getScheduledAt();

        boolean scheduled = scheduledAt != null;

        Optional<User> maybeDriver = findBestDriver(dto, passengers.size());

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

        for (User lp : linkedPassengers) {
            if (lp.getEmail() != null) {
                mailService.sendAddedToRideMail(lp.getEmail(), ride.getId());
            }
        }

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
    public RideResponseDTO getCurrentRideForDriver(Long driverId) {
        User u = userService.findById(driverId);

        if (u.getRole() != UserRole.DRIVER) {
            throw new NotFoundException("Driver not found");
        }

        return rideRepository.findFirstByDriverIdAndStatusInOrderByCreatedAtDesc(driverId,List.of(RideStatus.ASSIGNED, RideStatus.ACTIVE)).map(rideMapper::toResponseDTO).orElse(null);
    }
    
    @Override
    public Collection<RideResponseDTO> getScheduledRidesForUser(Long userId) {
        if (userService.findById(userId) == null) {
            throw new NotFoundException("User not found");
        }

        Collection<Ride> rides = rideRepository.findScheduledByUserId(userId,RideStatus.SCHEDULED);

        return rides.stream().map(rideMapper::toResponseDTO).collect(Collectors.toList());
    }
    
    @Override
    public RideResponseDTO startRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(()-> new NotFoundException("Ride not found"));
        User driver = ride.getDriver();
        User createdBy = ride.getCreatedBy();
        List<User> passengers = ride.getPassengers();

        if (ride.getStatus() != RideStatus.ASSIGNED && ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException("Ride cannot be started");
        }
        
        driver.setDriverStatus(DriverStatus.IN_RIDE);
        userRepository.save(driver);

        ride.setStatus(RideStatus.ACTIVE);
        ride.setStartTime(LocalDateTime.now());

        rideRepository.save(ride);

        driver.setDriverStatus(DriverStatus.IN_RIDE);
        userRepository.save(driver);

        Set<String> emails = new HashSet<>();

        if (createdBy != null && createdBy.getEmail() != null) {
            emails.add(createdBy.getEmail());
        }

        if (passengers != null) {
            for (User p : passengers) {
                if (p != null && p.getEmail() != null) {
                    emails.add(p.getEmail());
                }
            }
        }

        if (driver.getEmail() != null) {
            emails.remove(driver.getEmail());
        }

        for (String email : emails) {
            mailService.sendRideStartedMail(email, ride.getId());
        }


        return rideMapper.toResponseDTO(ride);
    }

    @Override
    public RideResponseDTO finishRide(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(()-> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException("Ride is not active");
        }

        User driver = ride.getDriver();
        User createdBy = ride.getCreatedBy();
        List<User> passengers = ride.getPassengers();

        ride.setStatus(RideStatus.FINISHED);
        ride.setEndTime(LocalDateTime.now());

        rideRepository.save(ride);

        driver.setDriverStatus(DriverStatus.ACTIVE);

        userRepository.save(driver);

        Set<String> emails = new HashSet<>();

        mailService.sendRideFinishedMail(createdBy.getEmail(), ride.getId() );

        if (passengers != null) {
            for (User p : passengers) {
                if (p != null && p.getEmail() != null) {
                    emails.add(p.getEmail());
                }
            }
        }

        if (driver.getEmail() != null) {
            emails.remove(driver.getEmail());
        }

        for (String email : emails) {
            mailService.sendRideFinishedMailLinkedPasengers(email, ride.getId());
        }




        return rideMapper.toResponseDTO(ride);
    }

    @Override
    public RideLiveInfoDTO getLiveInfo(Long rideId) {
        Ride ride = rideRepository.findById(rideId).orElseThrow(()-> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException("Ride is not active");
        }
        if (ride.getDriver() == null) {
            throw new BadRequestException("Ride has no assigned driver");
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

        userService.findById(driverId);
        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException("Invalid date range");
        }

        LocalDateTime fromDt = (from == null) ? null : from.atStartOfDay();
        LocalDateTime toDt = (to == null) ? null : to.plusDays(1).atStartOfDay().minusNanos(1);

        List<Ride> rides;

        if (fromDt == null && toDt == null) {
            rides = (List<Ride>) rideRepository.findByDriverIdAndStatusOrderByCreatedAtDesc(driverId, RideStatus.FINISHED);
        } else if (fromDt != null && toDt == null) {
            rides = (List<Ride>) rideRepository.findByDriverIdAndStatusAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
                    driverId, RideStatus.FINISHED, fromDt);
        } else if (fromDt == null && toDt != null) {
            rides = (List<Ride>) rideRepository.findByDriverIdAndStatusAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
                    driverId, RideStatus.FINISHED, toDt);
        } else {
            rides = (List<Ride>) rideRepository.findByDriverIdAndStatusAndCreatedAtBetweenOrderByCreatedAtDesc(
                    driverId, RideStatus.FINISHED, fromDt, toDt);
        }

        // --------- (1) all  userIds ----------
        Set<Long> ids = new HashSet<>();

        for (Ride r : rides) {
            // creator
            if (r.getCreatedBy() != null && r.getCreatedBy().getId() != null) {
                ids.add(r.getCreatedBy().getId());
            }

            // passengers
            if (r.getPassengers() != null) {
                for (User p : r.getPassengers()) {
                    if (p != null && p.getId() != null) ids.add(p.getId());
                }
            }
        }

        // --------- (2) batch fetch email map ----------
        Map<Long, String> emailById = ids.isEmpty()
                ? Map.of()
                : userRepository.findByIdIn(ids).stream()
                .collect(Collectors.toMap(User::getId, User::getEmail));

        // --------- (3) filling  DTO ----------
        return rides.stream()
                .map(ride -> {
                    RideResponseDTO dto = rideMapper.toResponseDTO(ride);

                    // creatorEmail
                    Long creatorId = dto.getCreatorId();
                    String creatorEmail = (creatorId == null) ? null : emailById.get(creatorId);
                    dto.setCreatorEmail(creatorEmail);

                    // passengerEmails
                    List<Long> pids = dto.getPassengerIds();
                    List<String> passengerEmails;

                    if (pids == null || pids.isEmpty()) {
                        passengerEmails = new java.util.ArrayList<>();
                    } else {
                        passengerEmails = pids.stream()
                                .map(emailById::get)
                                .filter(java.util.Objects::nonNull)
                                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
                    }

                    if (creatorEmail != null && !passengerEmails.contains(creatorEmail)) {
                        passengerEmails.add(0, creatorEmail);
                    }

                    dto.setPassengerEmails(passengerEmails);

                    return dto;
                })
                .toList();
    }

    private BigDecimal calculatePrice(VehicleType type, double distanceKm) {
        Pricing pricing = pricingRepository.findByVehicleType(type)
                .orElseThrow(() -> new NotFoundException("Pricing not found for vehicle type " + type.name()));

        BigDecimal base = BigDecimal.valueOf(pricing.getStartingPrice());
        BigDecimal perKm = BigDecimal.valueOf(pricing.getPricePerKm());

        return base.add(perKm.multiply(BigDecimal.valueOf(distanceKm)));
    }

 
    private Optional<User> findBestDriver(RideCreateDTO dto, int passengerCount) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = (dto.getScheduledAt() != null) ? dto.getScheduledAt() : now;

        int bufferMin = 10;
        LocalDateTime end = start.plusMinutes(dto.getEstimatedDurationMin() + bufferMin);

        List<User> candidates = userRepository.findAvailableDriversNoConflict(dto.getVehicleType().name(), passengerCount + 1, dto.isBabyFriendly(), dto.isPetFriendly(), start, end, bufferMin);

        if (candidates.isEmpty()) return Optional.empty();
        
        candidates = candidates.stream().filter(d -> driverActivityService.getWorkedMinutesLast24hAt(d, start) < MAX_MINUTES_LAST_24H).toList();

        boolean scheduled = dto.getScheduledAt() != null;

        if (scheduled) {
            return candidates.stream().min(Comparator.comparingLong(d ->rideRepository.countScheduledForDriverFrom(d.getId(), now)));
        }
        else {
        	 double lat = dto.getStartLat();
    	     double lng = dto.getStartLng();

    	     return candidates.stream()
	            .filter(d -> driverLocationService.getLiveLocation(d.getId()) != null)
	            .min(Comparator.comparingDouble(d -> {
	                DriverLocation loc = driverLocationService.getLiveLocation(d.getId());
	                return haversineKm(lat, lng, loc.getLat(), loc.getLng());
	            }));
		}
    }
    
    private static double haversineKm(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    public void cancelByDriver(Long rideId, DriverCancelRideDTO dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(()-> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ASSIGNED &&
                ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException("Ride cannot be cancelled");
        }

        if (ride.getScheduledAt() == null) {
            throw new BadRequestException("Ride cannot be cancelled");
        }


        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(dto.getReason());
        ride.setCancellationSource(CancellationSource.DRIVER);

        rideRepository.save(ride);

        notifyRideParticipants(ride, NotificationType.RIDE_CANCELLED);
    }

    public void cancelByPassenger(Long rideId, PassengerCancelRideDTO dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(()-> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ASSIGNED &&
                ride.getStatus() != RideStatus.SCHEDULED) {
            throw new BadRequestException("Ride cannot be cancelled");
        }

        if (ride.getScheduledAt() == null) {
            throw new BadRequestException("Ride cannot be cancelled");
        }

        if (LocalDateTime.now().isAfter(
                ride.getScheduledAt().minusMinutes(10))) {
            throw new BadRequestException("Ride cannot be cancelled less than 10 minutes before scheduled time");
        }


        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(dto.getReason());
        ride.setCancellationSource(CancellationSource.PASSENGER);

        rideRepository.save(ride);

        notifyRideParticipants(ride, NotificationType.RIDE_CANCELLED);
    }



    public StopRideResponseDTO stopRide(Long rideId, StopRideRequestDTO dto) {

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(()-> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException("Ride is not active");
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
                .orElseThrow(()-> new NotFoundException("Ride not found"));

        if (ride.getStatus() != RideStatus.ACTIVE) {
            throw new BadRequestException("Ride is not active");
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
                throw new BadRequestException("Invalid notification type");
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
                .orElseThrow(()-> new NotFoundException("Ride not found"));

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

    @Override
    public Optional<RidePassengerActiveDTO> getActiveRideForPassenger(Long userId) {
        return rideRepository.findActiveRideForPassenger(userId)
                .map(rideDTOMapper::toActiveResponseDTO); // Samo pozoveš maper
    }

}
