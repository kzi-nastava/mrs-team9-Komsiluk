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
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RegisterPassengerMapper;
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtService;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.AccountNotActivatedException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.EmailAlreadyExistsException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.InvalidCredentialsException;
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
    private final RegisterPassengerMapper registerPassengerMapper;

    public AuthService(
            IUserService userService,
            IUserTokenService userTokenService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            MailService mailService,
            RegisterPassengerMapper registerPassengerMapper) {
        this.userService = userService;
        this.userTokenService = userTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
        this.registerPassengerMapper = registerPassengerMapper;
    }

    @Override
    public void registerPassenger(RegisterPassengerRequestDTO dto) {

        User existing = userService.findByEmail(dto.getEmail());

        if (existing != null) {
            if (!existing.isActive()) {
                throw new AccountNotActivatedException();
            }
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = registerPassengerMapper.toEntity(dto);
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

        User user = userService.findByEmail(dto.getEmail());
        if (user == null) {
            throw new InvalidCredentialsException();
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        if (!user.isActive()) {
            throw new AccountNotActivatedException();
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


    @Override
    public void forgotPassword(String email) {

        User user = userService.findByEmail(email);

        if (user == null) {
            return;
        }

        UserTokenResponseDTO token =
                userTokenService.createPasswordResetToken(user.getId());

        mailService.sendPasswordResetMail(
                user.getEmail(),
                token.getToken()
        );
    }

}
