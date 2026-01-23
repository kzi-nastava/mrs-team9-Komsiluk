package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import org.springframework.web.bind.annotation.*;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing.PricingResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing.PricingUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IPricingService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/pricing")
public class PricingAdminController {

    private final IPricingService pricingService;

    public PricingAdminController(IPricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping
    public List<PricingResponseDTO> getAll() {
        return pricingService.getAll();
    }

    @PutMapping("/{type}")
    public PricingResponseDTO update(@PathVariable VehicleType type,
                                     @RequestBody PricingUpdateDTO dto) {
        return pricingService.update(type, dto);
    }
}

