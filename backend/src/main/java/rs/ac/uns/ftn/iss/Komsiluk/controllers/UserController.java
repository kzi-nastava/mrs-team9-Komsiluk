package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    
    @GetMapping("/{id}/blocked")
    public ResponseEntity<UserBlockedDTO> isUserBlocked(@PathVariable Long id) {
        boolean blocked = userService.isBlocked(id);
        UserBlockedDTO dto = new UserBlockedDTO();
        dto.setBlocked(blocked);
        return ResponseEntity.ok(dto);
    }
}