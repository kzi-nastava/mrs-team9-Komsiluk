package rs.ac.uns.ftn.iss.Komsiluk.s1.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import jakarta.persistence.EntityManager;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Pricing;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.PricingRepository;

@DataJpaTest
@ActiveProfiles("test")
class PricingRepositoryTest {

    @Autowired
    private PricingRepository pricingRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findByVehicleType_returnsPricing_whenExists() {
        persistPricing(VehicleType.STANDARD, 150, 80);

        Optional<Pricing> found = pricingRepository.findByVehicleType(VehicleType.STANDARD);

        assertTrue(found.isPresent());
        assertEquals(VehicleType.STANDARD, found.get().getVehicleType());
        assertEquals(150, found.get().getStartingPrice());
        assertEquals(80, found.get().getPricePerKm());
        assertNotNull(found.get().getUpdatedAt());
    }

    @Test
    void findByVehicleType_returnsEmpty_whenDoesNotExist() {
        persistPricing(VehicleType.STANDARD, 150, 80);

        Optional<Pricing> found = pricingRepository.findByVehicleType(VehicleType.LUXURY);

        assertFalse(found.isPresent());
    }
    
    
    
    // ---------------- helper ----------------

    private Pricing persistPricing(VehicleType type, int startingPrice, int pricePerKm) {
        Pricing pricing = new Pricing();
        pricing.setVehicleType(type);
        pricing.setStartingPrice(startingPrice);
        pricing.setPricePerKm(pricePerKm);

        entityManager.persist(pricing);
        entityManager.flush();
        return pricing;
    }
}
