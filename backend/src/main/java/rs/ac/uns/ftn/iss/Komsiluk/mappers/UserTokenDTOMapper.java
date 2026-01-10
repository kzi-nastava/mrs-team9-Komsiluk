package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.UserToken;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenResponseDTO;

@Component
public class UserTokenDTOMapper {
	
	private final ModelMapper modelMapper;

    @Autowired
    public UserTokenDTOMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // UserToken -> ResponseDTO
    public UserTokenResponseDTO toResponseDTO(UserToken token) {
        if (token == null) return null;
        return modelMapper.map(token, UserTokenResponseDTO.class);
    }
}
