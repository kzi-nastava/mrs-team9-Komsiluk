package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.security.Principal;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.push.PushTokenRegisterDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.PushNotificationService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private final PushNotificationService pushService;
    private final IUserService userService;

    public PushController(PushNotificationService pushService, IUserService userService) {
        this.pushService = pushService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody PushTokenRegisterDTO dto, Principal principal) {
        String email = principal.getName();

        var user = userService.findByEmail(email);
        if (user == null)
        	return ResponseEntity.badRequest().body("User not found");

        pushService.registerToken(user, dto.getToken());
        return ResponseEntity.ok().build();
    }
}
