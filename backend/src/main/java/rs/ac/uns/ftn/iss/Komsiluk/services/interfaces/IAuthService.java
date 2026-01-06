package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.RegisterPassengerRequestDTO;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    public void registerPassenger(RegisterPassengerRequestDTO dto);
}