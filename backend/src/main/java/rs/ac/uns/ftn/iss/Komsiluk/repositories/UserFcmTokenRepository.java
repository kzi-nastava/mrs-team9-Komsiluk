package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import rs.ac.uns.ftn.iss.Komsiluk.beans.UserFcmToken;

public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, Long> {

    Optional<UserFcmToken> findByToken(String token);

    @Query("select t.token from UserFcmToken t where t.user.id = :userId")
    List<String> findTokensByUserId(Long userId);

    void deleteByToken(String token);
}
