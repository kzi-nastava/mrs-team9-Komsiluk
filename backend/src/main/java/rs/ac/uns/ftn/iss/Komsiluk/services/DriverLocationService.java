package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.location.DriverLocationResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.DriverLocationRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverLocationService;

@Service
public class DriverLocationService implements IDriverLocationService {

    private static final double DEFAULT_LAT = 45.2671;
    private static final double DEFAULT_LNG = 19.8335;

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
        List<Long> activeIds = userRepository.findDriverIdsByStatus(UserRole.DRIVER, DriverStatus.ACTIVE);
        List<Long> inRideIds = userRepository.findDriverIdsByStatus(UserRole.DRIVER, DriverStatus.IN_RIDE);

        // spoji + ukloni duplikate
        List<Long> ids = new ArrayList<>();
        ids.addAll(activeIds);
        ids.addAll(inRideIds);

        ids = ids.stream().distinct().toList();
        if (ids.isEmpty()) return List.of();

        // uzmi postojeće lokacije
        List<DriverLocation> existing = (List<DriverLocation>) locationRepository.findByDriverIdIn(ids);

        // ako neki aktivan driver nema lokaciju u tabeli -> ubaci default (da se vidi na mapi)
        Set<Long> existingIds = existing.stream().map(DriverLocation::getDriverId).collect(Collectors.toSet());
        List<DriverLocation> toCreate = new ArrayList<>();
        for (Long id : ids) {
            if (!existingIds.contains(id)) {
                toCreate.add(new DriverLocation(id, DEFAULT_LAT, DEFAULT_LNG, LocalDateTime.now()));
            }
        }
        if (!toCreate.isEmpty()) {
            locationRepository.saveAll(toCreate);
            existing.addAll(toCreate);
        }

        return existing;
    }
    @Override
    public DriverLocationResponseDTO getLiveLocationDto(Long driverId) {
        DriverLocation loc = getLiveLocation(driverId);
        User driver = userRepository.findById(driverId).orElse(null);
        boolean busy = driver != null && driver.getDriverStatus() == DriverStatus.IN_RIDE;

        return toDto(loc, busy);
    }

    @Override
    public Collection<DriverLocationResponseDTO> getAllLiveLocationsDto() {
        Collection<DriverLocation> locs = getAllLiveLocations();

        // za svaki driverId proveri status i mapiraj
        return locs.stream().map(loc -> {
            User driver = userRepository.findById(loc.getDriverId()).orElse(null);
            boolean busy = driver != null && driver.getDriverStatus() == DriverStatus.IN_RIDE;
            return toDto(loc, busy);
        }).toList();
    }

    private DriverLocationResponseDTO toDto(DriverLocation loc, boolean busy) {
        DriverLocationResponseDTO dto = new DriverLocationResponseDTO();
        dto.setDriverId(loc.getDriverId());
        dto.setLat(loc.getLat());
        dto.setLng(loc.getLng());
        dto.setUpdatedAt(loc.getUpdatedAt());
        dto.setBusy(busy);
        return dto;
    }


}
