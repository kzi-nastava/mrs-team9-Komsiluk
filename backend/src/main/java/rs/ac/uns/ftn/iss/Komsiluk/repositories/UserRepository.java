package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;

@Repository
public class UserRepository {

	private final Map<Long, User> storage = new HashMap<>();
	private final AtomicLong idGenerator = new AtomicLong(0);

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(idGenerator.incrementAndGet());
        }
        storage.put(user.getId(), user);
        return user;
    }

    public User findById(Long id) {
        return storage.get(id);
    }

    public Collection<User> findAll() {
        return storage.values();
    }

    public void deleteById(Long id) {
        storage.remove(id);
    }

    public boolean existsByEmail(String email) {
        return storage.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email));
    }

    public User findByEmail(String email) {
        return storage.values().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null);
    }
}
