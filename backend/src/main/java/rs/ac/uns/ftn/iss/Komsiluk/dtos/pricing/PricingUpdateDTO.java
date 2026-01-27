package rs.ac.uns.ftn.iss.Komsiluk.dtos.pricing;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PricingUpdateDTO(
        @NotNull
        @Positive
        Integer startingPrice,
        @NotNull
        @Positive
        Integer pricePerKm
) { }
