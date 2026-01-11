package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.UserToken;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.TokenType;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

    public Optional<UserToken> findByToken(String tokenValue);

    public List<UserToken> findAllByUserIdAndType(Long userId, TokenType type);

    public List<UserToken> findAllByUserIdAndTypeAndUsedFalse(Long userId, TokenType type);

}
