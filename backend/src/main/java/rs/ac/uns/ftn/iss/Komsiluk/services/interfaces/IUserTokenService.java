package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import java.util.Collection;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.userToken.UserTokenResponseDTO;

public interface IUserTokenService {

	public Collection<UserTokenResponseDTO> getAll();

    public UserTokenResponseDTO getOne(Long id);

    public UserTokenResponseDTO createActivationToken(Long id);

    public UserTokenResponseDTO activateWithPassword(String tokenValue, String rawPassword);
}
