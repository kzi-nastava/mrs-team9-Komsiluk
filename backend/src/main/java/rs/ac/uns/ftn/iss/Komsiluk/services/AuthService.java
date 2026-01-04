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
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IAuthService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;

@Service
public class AuthService implements IAuthService {

    private final IUserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(IUserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
