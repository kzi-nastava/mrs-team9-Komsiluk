package rs.ac.uns.ftn.iss.Komsiluk.controllers;

import java.util.Collection;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.ResetPasswordRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenActivationDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenPassengerActivationDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;

@RestController
@RequestMapping(value = "/api/tokens")
public class UserTokenController {

	@Autowired
    private IUserTokenService userTokenService;

	// debug
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserTokenResponseDTO>> getAll() {
        return new ResponseEntity<>(userTokenService.getAll(), HttpStatus.OK);
    }

    // debug
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenResponseDTO> getOne(@PathVariable Long id) {
        return new ResponseEntity<>(userTokenService.getOne(id), HttpStatus.OK);
    }

    // debug
    @PostMapping(value = "/creation/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTokenResponseDTO> createActivationToken(@PathVariable Long id) {
        UserTokenResponseDTO created = userTokenService.createActivationToken(id);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

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
