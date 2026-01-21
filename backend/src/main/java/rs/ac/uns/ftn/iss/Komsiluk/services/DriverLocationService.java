package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.DriverLocationRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverLocationService;

@Service
public class DriverLocationService implements IDriverLocationService {

    @Autowired
    private DriverLocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;


    @Override
    public void updateLiveLocation(Long driverId, double lat, double lng) {
        DriverLocation loc = new DriverLocation(driverId, lat, lng, LocalDateTime.now());
        locationRepository.save(loc);
    }

    @Override
    public DriverLocation getLiveLocation(Long driverId) {
        return locationRepository.findById(driverId).orElse(null);
    }

    @Override
    public Collection<DriverLocation> getAllLiveLocations() {
        return getActiveDriverLocations();
    }


    public Collection<DriverLocation> getActiveDriverLocations() {
        List<Long> activeDriverIds =
                userRepository.findDriverIdsByStatus(UserRole.DRIVER, DriverStatus.ACTIVE);

        if (activeDriverIds.isEmpty()) return List.of();

        return locationRepository.findByDriverIdIn(activeDriverIds);
    }



}
