package rs.ac.uns.ftn.iss.Komsiluk.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.DriverStatus;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.RegisterPassengerRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.RegisterPassengerMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserRepository;
import rs.ac.uns.ftn.iss.Komsiluk.security.jwt.JwtService;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.AccountNotActivatedException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.EmailAlreadyExistsException;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.PasswordsDoNotMatchException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IAuthService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;

import javax.management.relation.Role;

@Service
public class AuthService implements IAuthService {

    private final IUserService userService;
    private final IUserTokenService userTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    private final RegisterPassengerMapper registerPassengerMapper;
    private final AuthenticationManager authenticationManager;
    private final DriverActivityService driverActivityService;
    private final UserRepository userRepository;

    @Value("${app.user.default-profile-image}")
    private String defaultProfileImageUrl;

    public AuthService(
            IUserService userService,
            IUserTokenService userTokenService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            MailService mailService,
            RegisterPassengerMapper registerPassengerMapper,
            AuthenticationManager authenticationManager, DriverActivityService driverActivityService, UserRepository userRepository) {
        this.userService = userService;
        this.userTokenService = userTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.mailService = mailService;
        this.registerPassengerMapper = registerPassengerMapper;
        this.authenticationManager = authenticationManager;
        this.driverActivityService = driverActivityService;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void registerPassenger(RegisterPassengerRequestDTO dto,
                                  MultipartFile profileImage) {


        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();
        }

        User existing = userService.findByEmail(dto.getEmail());

        if (existing != null) {
            if (!existing.isActive()) {
                throw new AccountNotActivatedException();
            }
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = registerPassengerMapper.toEntity(dto);

        user.setProfileImageUrl(defaultProfileImageUrl);

        userService.save(user);

        if (profileImage != null && !profileImage.isEmpty()) {
            userService.updateProfileImage(user.getId(), profileImage);
        }

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
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword()));

            User user = (User) authentication.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtService.generateAccessToken(user);

            if (user.getRole() == UserRole.DRIVER) {

                long workedMinutes = driverActivityService
                        .getWorkedMinutesLast24h(user);

                if (workedMinutes >= 480) {
                    user.setDriverStatus(DriverStatus.INACTIVE);
                } else {
                    user.setDriverStatus(DriverStatus.ACTIVE);
                }
            }
            userRepository.save(user);
            return new LoginResponseDTO(
                    token,
                    user.getId(),
                    user.getEmail(),
                    user.getRole(),
                    user.getDriverStatus()
            );

        }
        catch (AuthenticationException ex) {
            throw ex;
        }
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
