package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {
    Collection<DriverLocation> findByDriverIdIn(Collection<Long> driverIds);
    Collection<DriverLocation> findAllByOrderByUpdatedAtDesc();
    Collection<DriverLocation> findByUpdatedAtAfter(LocalDateTime after);
    Optional<DriverLocation> findByDriverId(Long driverId);

}
