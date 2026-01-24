package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.location.DriverLocationResponseDTO;

public interface IDriverLocationService {

    void updateLiveLocation(Long driverId, double lat, double lng);

    DriverLocation getLiveLocation(Long driverId);

    Collection<DriverLocation> getAllLiveLocations();

    DriverLocationResponseDTO getLiveLocationDto(Long driverId);

    Collection<DriverLocationResponseDTO> getAllLiveLocationsDto();
}
