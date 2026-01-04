package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginResponseDTO;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
}