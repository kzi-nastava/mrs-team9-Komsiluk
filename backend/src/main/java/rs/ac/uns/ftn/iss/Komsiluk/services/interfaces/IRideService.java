package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.time.LocalDate;
import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.AdminRideSortBy;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.RideLiveInfoDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.ride.*;

public interface IRideService {
	
	public Collection<RideResponseDTO> getScheduledRidesForUser(Long userId);

    public RideResponseDTO orderRide(RideCreateDTO dto);
    
    public RideResponseDTO startRide(Long rideId);

    public RideResponseDTO finishRide(Long rideId);

    public void cancelByDriver(Long rideId, DriverCancelRideDTO dto);

    public void cancelByPassenger(Long rideId, PassengerCancelRideDTO dto);

    public StopRideResponseDTO stopRide(Long id,StopRideRequestDTO dto);

    public RideLiveInfoDTO getLiveInfo(Long rideId);

    public Collection<RideResponseDTO> getDriverRideHistory(Long driverId, LocalDate from, LocalDate to);

//    public Collection<AdminRideHistoryDTO> getAdminRideHistory(LocalDate from,LocalDate to,String sortBy);

    public void handlePanicButton(Long rideId, PanicRequestDTO driverId);

    public AdminRideDetailsDTO getAdminRideDetails(Long rideId);

    public Collection<AdminRideHistoryDTO> getAdminRideHistoryForUser(Long userId, LocalDate from, LocalDate to, AdminRideSortBy sortBy);
}