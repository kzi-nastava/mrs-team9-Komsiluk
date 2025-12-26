package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserChangePasswordDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileUpdateDTO;

public interface IUserService {

	public User findById(Long id);

    public Collection<User> findAll();

    public User save(User user);
    
    public void delete(Long id);
    
    public UserProfileResponseDTO getProfile(Long id);
    
    public UserProfileResponseDTO updateProfile(Long id, UserProfileUpdateDTO userProfileUpdateDTO);
    
    public void changePassword(Long id, UserChangePasswordDTO userChangePasswordDTO);
    
    public User findByEmail(String email);
}
