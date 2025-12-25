package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.TokenType;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserToken;
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
}
