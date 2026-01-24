package rs.ac.uns.ftn.iss.Komsiluk.controllers;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.*;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IAuthService;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(
            IAuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }


    @PostMapping(
            value = "/registration/passenger",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RegisterResponseDTO> registerPassenger(
            @Valid @RequestPart("data") RegisterPassengerRequestDTO requestDto,
            @RequestPart(value="profileImage", required = false) MultipartFile profileImage
    ) {

        authService.registerPassenger(requestDto, profileImage);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RegisterResponseDTO(
                        "Activation link has been sent to your email."
                ));
    }

    @PostMapping("/registration/resend")
    public ResponseEntity<Void> resendActivation(
            @Valid @RequestBody ResendActivationDTO dto) {

        authService.resendActivation(dto.getEmail());
        return ResponseEntity.ok().build();
    }



    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto) {

        authService.forgotPassword(dto.getEmail());
        return ResponseEntity.ok().build();
    }
}
