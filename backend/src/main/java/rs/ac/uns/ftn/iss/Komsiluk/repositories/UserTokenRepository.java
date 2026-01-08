package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.TokenType;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.UserToken;

@Repository
public class UserTokenRepository {

	private final Map<Long, UserToken> storage = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public UserToken save(UserToken token) {
        if (token.getId() == null) {
            token.setId(idGenerator.incrementAndGet());
        }
        storage.put(token.getId(), token);
        return token;
    }

    public Optional<UserToken> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<UserToken> findByToken(String tokenValue) {
        return storage.values().stream().filter(t -> t.getToken().equals(tokenValue)).findFirst();
    }

    public List<UserToken> findAllByUserIdAndType(Long userId, TokenType type) {
        return storage.values().stream()
                .filter(token -> token.getUser() != null)
                .filter(token -> token.getUser().getId().equals(userId))
                .filter(token -> token.getType() == type)
                .toList();
    }


    public List<UserToken> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }
}
