package rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.VehicleType;

public record PricingResponseDTO(
        VehicleType vehicleType,
        Integer startingPrice,
        Integer pricePerKm
) {}

