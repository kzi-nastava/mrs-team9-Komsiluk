package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.DriverLocationRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverLocationService;

@Service
public class DriverLocationService implements IDriverLocationService {

    private static final double DEFAULT_LAT = 45.2671;
    private static final double DEFAULT_LNG = 19.8335;

    @Autowired
    private DriverLocationRepository locationRepository;

    @Override
    public void updateLiveLocation(Long driverId, double lat, double lng) {
        DriverLocation loc = new DriverLocation(driverId, lat, lng, LocalDateTime.now());
        locationRepository.putLive(loc);
    }

    @Override
    public DriverLocation getLiveLocation(Long driverId) {
        return locationRepository.getLive(driverId);
    }

    @Override
    public Collection<DriverLocation> getAllLiveLocations() {
        return locationRepository.getAllLive();
    }

    @Override
    public void onDriverBecameActive(Long driverId) {
        DriverLocation last = locationRepository.getLastKnown(driverId);
        if (last == null) {
            last = new DriverLocation(driverId, DEFAULT_LAT, DEFAULT_LNG, LocalDateTime.now());
        } else {
            last.setUpdatedAt(LocalDateTime.now());
        }
        locationRepository.putLive(last);
    }

    @Override
    public void onDriverBecameInactive(Long driverId) {
        DriverLocation live = locationRepository.getLive(driverId);
        if (live != null) {
            locationRepository.saveLastKnown(live);
        }
        locationRepository.removeLive(driverId);
    }
}
