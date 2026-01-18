package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import org.springframework.web.multipart.MultipartFile;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginRequestDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.LoginResponseDTO;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.RegisterPassengerRequestDTO;

public interface IAuthService {
    LoginResponseDTO login(LoginRequestDTO dto);
    public void registerPassenger(RegisterPassengerRequestDTO dto, MultipartFile profileImage);
    public void resendActivation(String email);
    void forgotPassword(String email);
}