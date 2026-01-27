package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing.PricingResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing.PricingUpdateDTO;

import java.util.List;

public interface IPricingService {
    List<PricingResponseDTO> getAll();
    PricingResponseDTO update(VehicleType type, PricingUpdateDTO dto);
}

