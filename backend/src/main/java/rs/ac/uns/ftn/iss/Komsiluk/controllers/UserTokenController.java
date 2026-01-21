package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.ResetPasswordRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenActivationDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenPassengerActivationDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;

@RestController
@RequestMapping(value = "/api/tokens")
public class UserTokenController {

	@Autowired
    private IUserTokenService userTokenService;

    @PostMapping(value = "/activation", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> activate(@RequestBody UserTokenActivationDTO dto) {
        userTokenService.activateWithPassword(dto.getToken(), dto.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/activation/passenger",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> activatePassenger(
            @Valid @RequestBody UserTokenPassengerActivationDTO dto) {

        userTokenService.activate(dto.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping(
            value = "/reset-password",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto) {

        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            return ResponseEntity.badRequest().build();
        }

        userTokenService.resetPassword(dto.getToken(), dto.getNewPassword());

        return ResponseEntity.ok().build();
    }


}
