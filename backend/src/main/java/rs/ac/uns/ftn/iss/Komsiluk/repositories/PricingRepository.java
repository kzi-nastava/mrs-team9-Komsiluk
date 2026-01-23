package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Pricing;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

import java.util.Optional;

public interface PricingRepository extends JpaRepository<Pricing, Long> {
    Optional<Pricing> findByVehicleType(VehicleType vehicleType);
}

