package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.DriverLocation;

import java.util.Collection;

@Repository
public interface DriverLocationRepository extends JpaRepository<DriverLocation, Long> {
    Collection<DriverLocation> findByDriverIdIn(Collection<Long> driverIds);
}
