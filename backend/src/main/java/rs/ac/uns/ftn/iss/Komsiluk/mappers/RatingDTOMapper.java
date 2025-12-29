package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Rating;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.rating.RatingResponseDTO;

@Component
public class RatingDTOMapper {

    private final ModelMapper modelMapper = new ModelMapper();

    public RatingResponseDTO toResponseDTO(Rating rating) {
        return modelMapper.map(rating, RatingResponseDTO.class);
    }
}
