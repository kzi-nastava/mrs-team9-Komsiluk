package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;

public interface IDriverLocationService {

    void updateLiveLocation(Long driverId, double lat, double lng);

    DriverLocation getLiveLocation(Long driverId);

    Collection<DriverLocation> getAllLiveLocations();

    void onDriverBecameActive(Long driverId);

    void onDriverBecameInactive(Long driverId);
}
