package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileUpdateDTO;

@Component
public class UserDTOMapper {

	private final ModelMapper modelMapper;

	@Autowired
	public UserDTOMapper() {
		this.modelMapper = new ModelMapper();
	}
	
	// User -> UserProfileResponseDTO
	public UserProfileResponseDTO toResponseDTO(User user) {
		return modelMapper.map(user, UserProfileResponseDTO.class);
	}
	
	// UserProfileUpdateDTO -> User
	public void fromUpdateDTO(UserProfileUpdateDTO dto, User user) {
		String imageUrl = user.getProfileImageUrl();
		modelMapper.map(dto, user);
		user.setProfileImageUrl(imageUrl);
	}
}
