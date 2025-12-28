package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Route;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.DriverResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideEstimateRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideEstimateResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.route.RouteResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RideDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RouteDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverActivityService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.INotificationService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRideService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IRouteService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

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
        passengers.add(creator);

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
            notificationService.createNotification(notificationDTOCreator);
        }

        return rideMapper.toResponseDTO(ride);
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

    public RideEstimateResponseDTO estimate(RideEstimateRequestDTO dto) {
        RideEstimateResponseDTO response = new RideEstimateResponseDTO();

        // ovde neka logika za estimaciju
        response.setDistanceKm(5);
        response.setEstimatedDurationMin(10);
        response.setStartAddress(dto.getStartAddress());
        response.setDestinationAddress(dto.getDestinationAddress());

        return response;
    }
}
