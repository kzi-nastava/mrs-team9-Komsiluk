package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserChangePasswordDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.UserDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.InvalidPasswordException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@Service
public class UserService implements IUserService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
	private UserDTOMapper userMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Override
    public User findById(Long id) {
        User user = userRepository.findById(id);
        if (user == null) {
            throw new NotFoundException();
        }
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return userRepository.findAll();
    }
    
    @Override
    public User save(User user) {
		return userRepository.save(user);
	}
    
    @Override
    public void delete(Long id) {
		User user = userRepository.findById(id);
		if (user == null) {
			throw new NotFoundException();
		}
		userRepository.deleteById(id);
	}
    
    @Override
    public UserProfileResponseDTO getProfile(Long id) {
		User user = userRepository.findById(id);
		if (user == null) {
			throw new NotFoundException();
		}
		return userMapper.toResponseDTO(user);
	}
    
    @Override
    public UserProfileResponseDTO updateProfile(Long id, UserProfileUpdateDTO profileDTO) {
		User user = userRepository.findById(id);
		if (user == null) {
			throw new NotFoundException();
		}
		userMapper.fromUpdateDTO(profileDTO, user);
		
		userRepository.save(user);
		
		return userMapper.toResponseDTO(user);
    }

	@Override
	public void changePassword(Long id, UserChangePasswordDTO dto) {
		User user = userRepository.findById(id);
		
		if (user == null) {
			throw new NotFoundException();
		}
		if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash())) {
            throw new InvalidPasswordException();
        }
		
		user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
		
		userRepository.save(user);
	}
	
	@Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
}
