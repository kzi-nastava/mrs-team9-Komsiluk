package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.iss.Komsiluk.beans.ProfileChangeRequest;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.Vehicle;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.driver.ProfileChangeRequestResponseDTO;

@Component
public class ProfileChangeRequestDTOMapper {
	
	private final ModelMapper modelMapper;
	
	@Autowired
	public ProfileChangeRequestDTOMapper(ModelMapper modelMapper) {
		this.modelMapper = modelMapper;
	}

	// ProfileChangeRequestCreateDTO -> ProfileChangeRequest
	public ProfileChangeRequest fromCreateDTO(ProfileChangeRequestCreateDTO dto, ProfileChangeRequest entity) {
		modelMapper.map(dto, entity);
		return entity;
	}
	
	// ProfileChangeRequest -> ProfileChangeRequestResponseDTO
	public ProfileChangeRequestResponseDTO toResponseDTO(ProfileChangeRequest entity) {
		return modelMapper.map(entity, ProfileChangeRequestResponseDTO.class);
	}
	
	// ProfileChangeRequest -> User (driver)
	public void fromRequestToUser(ProfileChangeRequest request, User user) {
		user.setFirstName(request.getNewName());
		user.setLastName(request.getNewSurname());
		user.setPhoneNumber(request.getNewPhoneNumber());
		user.setAddress(request.getNewAddress());
		user.setCity(request.getNewCity());
		user.setProfileImageUrl(request.getNewProfileImageUrl());
	}
	
	// ProfileChangeRequest -> Vehicle
	public void fromRequestToVehicle(ProfileChangeRequest request, Vehicle vehicle) {
		vehicle.setLicencePlate(request.getNewLicencePlate());
		vehicle.setModel(request.getNewModel());
		vehicle.setType(request.getNewType());
		vehicle.setSeatCount(request.getNewSeatCount());
		vehicle.setPetFriendly(request.getNewPetFriendly());
		vehicle.setBabyFriendly(request.getNewBabyFriendly());
	}
}
