package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.TokenType;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserToken;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserChangePasswordDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.user.UserResetPasswordDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.mappers.UserTokenDTOMapper;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserTokenRepository;
import rs.ac.uns.ftn.iss.Komsiluk.services.exceptions.NotFoundException;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserService;
import rs.ac.uns.ftn.iss.Komsiluk.services.interfaces.IUserTokenService;

@Service
public class UserTokenService implements IUserTokenService {

	@Autowired
	private UserTokenRepository userTokenRepository;
	@Autowired
    private UserTokenDTOMapper mapper;
	@Autowired
	private IUserService userService;
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Override
    public Collection<UserTokenResponseDTO> getAll() {
        return userTokenRepository.findAll().stream().map(mapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    public UserTokenResponseDTO getOne(Long id) {
        UserToken token = userTokenRepository.findById(id).orElseThrow(NotFoundException::new);
        return mapper.toResponseDTO(token);
    }

    @Override
    public UserTokenResponseDTO createActivationToken(Long id) {
        UserToken token = new UserToken();

        token.setType(TokenType.ACTIVATION);
        token.setUsed(false);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUser(userService.findById(id));

        UserToken saved = userTokenRepository.save(token);
        return mapper.toResponseDTO(saved);
    }

    public UserTokenResponseDTO createPasswordResetToken(Long userId) {

        User user = userService.findById(userId);

        // poništi sve prethodne PASSWORD_RESET tokene
        userTokenRepository.findAll().stream()
                .filter(t -> t.getUser().getId().equals(userId))
                .filter(t -> t.getType() == TokenType.PASSWORD_RESET)
                .filter(t -> !t.isUsed())
                .forEach(t -> {
                    t.setUsed(true);
                    userTokenRepository.save(t);
                });

        UserToken token = new UserToken();
        token.setType(TokenType.PASSWORD_RESET);
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUsed(false);

        UserToken saved = userTokenRepository.save(token);

        return mapper.toResponseDTO(saved);
    }


    @Override
    public UserTokenResponseDTO activateWithPassword(String tokenValue, String rawPassword) {
        UserToken token = userTokenRepository.findByToken(tokenValue).orElseThrow(NotFoundException::new);

        if (token.isUsed() || token.getType() != TokenType.ACTIVATION || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new NotFoundException();
        }
        
        Long userId = token.getUser().getId();
        User user = userService.findById(userId);

        String hashed = passwordEncoder.encode(rawPassword);
        user.setPasswordHash(hashed);
        user.setActive(true);

        userService.save(user);

        token.setUsed(true);
        UserToken saved = userTokenRepository.save(token);

        return mapper.toResponseDTO(saved);
    }

    public void activate(String tokenValue) {

        UserToken token = userTokenRepository.findByToken(tokenValue)
                .orElseThrow(NotFoundException::new);

        if (token.isUsed()
                || token.getType() != TokenType.ACTIVATION
                || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new NotFoundException();
        }

        User user = token.getUser();
        user.setActive(true);

        userService.save(user);

        token.setUsed(true);
        userTokenRepository.save(token);
    }

    public UserTokenResponseDTO resendActivationToken(Long userId) {

        List<UserToken> tokens =
                userTokenRepository.findAllByUserIdAndType(
                        userId,
                        TokenType.ACTIVATION
                );

        boolean hasValidToken = tokens.stream()
                .anyMatch(t ->
                        !t.isUsed() &&
                                t.getExpiresAt().isAfter(LocalDateTime.now())
                );

        if (hasValidToken) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Activation email already sent. Please check your inbox."
            );
        }

        return createActivationToken(userId);
    }



    public void resetPassword(String tokenValue, String newPassword) {

        UserToken token = userTokenRepository.findByToken(tokenValue)
                .orElseThrow(NotFoundException::new);

        if (token.isUsed()
                || token.getType() != TokenType.PASSWORD_RESET
                || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new NotFoundException();
        }

        User user = token.getUser();

        // delegacija UserService-u
        userService.resetPassword(user.getId(), newPassword);

        // invalidate token
        token.setUsed(true);
        userTokenRepository.save(token);
    }



}
