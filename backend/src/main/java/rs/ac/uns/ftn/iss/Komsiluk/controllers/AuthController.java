package rs.ac.uns.ftn.iss.Komsiluk.controllers;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.*;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IAuthService;



@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    private final IUserService userService;
    private final IUserTokenService userTokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            IAuthService authService,
            IUserService userService,
            IUserTokenService userTokenService,
            PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.userService = userService;
        this.userTokenService = userTokenService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }



    @PostMapping("/registration/passenger")
    public ResponseEntity<RegisterResponseDTO> register(
            @RequestBody RegisterPassengerRequestDTO dto) {

        User existing = userService.findByEmail(dto.getEmail());
        if (existing != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new RegisterResponseDTO("Email already exists"));
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setProfileImageUrl(
                dto.getProfileImageUrl() != null ? dto.getProfileImageUrl() : "/images/default.png"
        );
        user.setRole(UserRole.PASSENGER);
        user.setActive(false);
        user.setBlocked(false);

        userService.save(user);
        userTokenService.createActivationToken(user.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RegisterResponseDTO(
                        "Activation link has been sent to your email."
                ));
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @RequestBody ForgotPasswordRequestDTO dto) {

        try {
            User user = userService.findByEmail(dto.getEmail());

            userTokenService.createPasswordResetToken(user.getId());

            // ovde ce ici slanje maila

        } catch (Exception ignored) {
            // prazno da ne bismo otkrili da li email postoji
        }

        return ResponseEntity.ok().build();
    }
}
