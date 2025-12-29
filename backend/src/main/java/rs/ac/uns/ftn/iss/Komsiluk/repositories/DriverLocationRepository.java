package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;

@Repository
public class DriverLocationRepository {


    private final Map<Long, DriverLocation> liveCache = new ConcurrentHashMap<>();


    private final Map<Long, DriverLocation> lastKnownStorage = new ConcurrentHashMap<>();

    public void putLive(DriverLocation loc) {
        if (loc == null || loc.getDriverId() == null) return;
        liveCache.put(loc.getDriverId(), loc);
    }

    public DriverLocation getLive(Long driverId) {
        return liveCache.get(driverId);
    }

    public void removeLive(Long driverId) {
        liveCache.remove(driverId);
    }

    public Collection<DriverLocation> getAllLive() {
        return liveCache.values();
    }

    public void saveLastKnown(DriverLocation loc) {
        if (loc == null || loc.getDriverId() == null) return;
        lastKnownStorage.put(loc.getDriverId(), loc);
    }

    public DriverLocation getLastKnown(Long driverId) {
        return lastKnownStorage.get(driverId);
    }
}
