package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserChangePasswordDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.UserDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.InvalidPasswordException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IDriverActivityService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@Service
public class UserService implements IUserService {

	@Autowired
    private UserRepository userRepository;
	@Autowired
	private UserDTOMapper userMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private IDriverActivityService driverActivityService;

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
		
		UserProfileResponseDTO userResponseDTO = userMapper.toResponseDTO(user);
		
		if(user.getRole().name().equals("DRIVER")) {
			userResponseDTO.setActiveMinutesLast24h(driverActivityService.getWorkedMinutesLast24h(user));
		}
		
		return userResponseDTO;
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

    public void resetPassword(Long userId, String newPassword) {

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException();
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
    
    @Override
    public List<String> autocompleteEmails(String query, int limit) {
        String prefix = (query == null) ? "" : query.trim().toLowerCase();
        if (limit <= 0) {
            limit = 10;
        }

        Set<UserRole> allowedRoles = Set.of(UserRole.DRIVER, UserRole.PASSENGER);

        return userRepository.findAll().stream()
                .filter(u -> u.getEmail() != null).filter(u -> allowedRoles.contains(u.getRole()))
                .map(User::getEmail).map(String::trim)
                .filter(e -> e.toLowerCase().startsWith(prefix))
                .distinct().sorted().limit(limit)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isBlocked(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException();
        }
        return user.isBlocked();
    }
}
