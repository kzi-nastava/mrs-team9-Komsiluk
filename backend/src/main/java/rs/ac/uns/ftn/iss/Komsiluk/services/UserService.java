package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@Service
public class UserService implements IUserService {

	@Autowired
    private UserRepository userRepository;


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
}
