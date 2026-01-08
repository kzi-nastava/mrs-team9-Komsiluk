package rs.ac.uns.ftn.iss.Komsiluk.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.RegisterPassengerRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IAuthService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;

@Service
public class AuthService implements IAuthService {

    private final IUserService userService;
    private final IUserTokenService userTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    public AuthService(
            IUserService userService,
            IUserTokenService userTokenService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            MailService mailService) {
        this.userService = userService;
        this.userTokenService = userTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
    }

    @Override
    public void registerPassenger(RegisterPassengerRequestDTO dto) {

        if (userService.findByEmail(dto.getEmail()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
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
                dto.getProfileImageUrl() != null
                        ? dto.getProfileImageUrl()
                        : "/images/default.png"
        );
        user.setRole(UserRole.PASSENGER);
        user.setActive(false);
        user.setBlocked(false);

        userService.save(user);

        UserTokenResponseDTO token =
                userTokenService.createActivationToken(user.getId());

        mailService.sendActivationMail(
                user.getEmail(),
                token.getToken()
        );
    }

    public void resendActivation(String email) {
        User user = userService.findByEmail(email);

        if (user == null || user.isActive()) {
            return;
        }

        UserTokenResponseDTO token =
                userTokenService.resendActivationToken(user.getId());

        mailService.sendActivationMail(
                user.getEmail(),
                token.getToken()
        );
    }


    @Override
    public LoginResponseDTO login(LoginRequestDTO dto) {

        User user = findUserOrThrowUnauthorized(dto.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        if (user.getRole() == UserRole.DRIVER) {
            user.setDriverStatus(DriverStatus.ACTIVE);
            userService.save(user);
        }

        String accessToken = jwtService.generateAccessToken(user);

        return new LoginResponseDTO(
                accessToken,
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getDriverStatus()
        );
    }

    private User findUserOrThrowUnauthorized(String email) {
        try {
            User user = userService.findByEmail(email);
            if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
            return user;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }
}
