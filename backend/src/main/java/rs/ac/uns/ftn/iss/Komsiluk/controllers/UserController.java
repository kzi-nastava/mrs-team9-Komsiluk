package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserBlockedDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserChangePasswordDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserProfileUpdateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
    private IUserService userService;

    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponseDTO> getProfile(@PathVariable Long id) {
        UserProfileResponseDTO dto = userService.getProfile(id);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'PASSENGER')")
    @PutMapping("/{id}/profile")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(@PathVariable Long id, @RequestBody UserProfileUpdateDTO updateDTO) {
        UserProfileResponseDTO updated = userService.updateProfile(id, updateDTO);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody UserChangePasswordDTO dto) {
        userService.changePassword(id, dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @GetMapping("/emails/autocomplete")
    public ResponseEntity<List<String>> autocompleteEmails(@RequestParam(name = "query", required = false, defaultValue = "") String query, @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        List<String> emails = userService.autocompleteEmails(query, limit);
        return new ResponseEntity<>(emails, HttpStatus.OK);
    }
    
    @PreAuthorize("hasAnyRole('DRIVER', 'PASSENGER')")
    @GetMapping("/{id}/blocked")
    public ResponseEntity<UserBlockedDTO> isUserBlocked(@PathVariable Long id) {
        boolean blocked = userService.isBlocked(id);
        UserBlockedDTO dto = new UserBlockedDTO();
        dto.setBlocked(blocked);
        return ResponseEntity.ok(dto);
    }
    
    @PutMapping(value = "/{id}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserProfileResponseDTO updateProfileImage(@PathVariable Long id, @RequestPart("image") MultipartFile image) {
        return userService.updateProfileImage(id, image);
    }
}