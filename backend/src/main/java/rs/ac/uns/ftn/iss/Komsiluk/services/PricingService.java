package rs.ac.uns.ftn.iss.Komsiluk.services;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Pricing;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing.PricingUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.PricingRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.BadRequestException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;

import java.util.List;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing.PricingResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IPricingService;

import java.util.stream.Collectors;

@Service
public class PricingService implements IPricingService {
    private final PricingRepository repo;

    public PricingService(PricingRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<PricingResponseDTO> getAll() {
        // Pretvaramo listu Pricing entiteta u listu PricingResponseDTO rekorda
        return repo.findAll(Sort.by("vehicleType")).stream()
                .map(p -> new PricingResponseDTO(
                        p.getVehicleType(),
                        p.getStartingPrice(),
                        p.getPricePerKm()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public PricingResponseDTO update(VehicleType type, PricingUpdateDTO dto) {
        Pricing p = repo.findByVehicleType(type).orElseThrow(NotFoundException::new);

        // Tvoja validacija za Integer (veće od nule)
        if (dto.startingPrice() == null || dto.startingPrice() <= 0) {
            throw new BadRequestException();
        }
        if (dto.pricePerKm() == null || dto.pricePerKm() <= 0) {
            throw new BadRequestException();
        }

        p.setStartingPrice(dto.startingPrice());
        p.setPricePerKm(dto.pricePerKm());
        repo.save(p);

        return new PricingResponseDTO(p.getVehicleType(), p.getStartingPrice(), p.getPricePerKm());
    }
}
