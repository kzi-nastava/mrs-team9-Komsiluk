package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    public boolean existsByLicencePlateIgnoreCase(String licencePlate);
}
