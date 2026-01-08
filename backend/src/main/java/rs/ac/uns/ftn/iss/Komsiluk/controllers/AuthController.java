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
    private final IUserTokenService userTokenService;
    private final IUserService userService;

    public AuthController(
            IAuthService authService,
            IUserService userService,
            IUserTokenService userTokenService) {
        this.authService = authService;
        this.userTokenService = userTokenService;
        this.userService = userService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/registration/passenger")
    public ResponseEntity<RegisterResponseDTO> registerPassenger(
            @RequestBody RegisterPassengerRequestDTO dto) {

        authService.registerPassenger(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterResponseDTO(
                        "Activation link has been sent to your email."
                ));
    }

    @PostMapping("/registration/resend")
    public ResponseEntity<Void> resendActivation(
            @RequestBody ResendActivationDTO dto) {

        authService.resendActivation(dto.getEmail());
        return ResponseEntity.ok().build();
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
