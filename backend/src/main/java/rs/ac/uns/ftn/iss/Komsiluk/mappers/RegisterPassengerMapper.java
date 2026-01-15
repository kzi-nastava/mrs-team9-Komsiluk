package rs.ac.uns.ftn.iss.Komsiluk.mappers;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserRole;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.auth.RegisterPassengerRequestDTO;

@Component
public class RegisterPassengerMapper {

    private final PasswordEncoder passwordEncoder;

    public RegisterPassengerMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(RegisterPassengerRequestDTO dto) {
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


        return user;
    }
}

