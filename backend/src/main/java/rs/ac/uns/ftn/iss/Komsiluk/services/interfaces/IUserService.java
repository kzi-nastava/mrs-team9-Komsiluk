package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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

    public void resetPassword(Long userId, String newPassword);

    public User findByEmail(String email);
    
    public List<String> autocompleteEmails(String query, int limit);
    
    public boolean isBlocked(Long userId);
    
    public UserProfileResponseDTO updateProfileImage(Long id, MultipartFile image);
}
