package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.FavoriteRoute;

@Repository
public class FavoriteRouteRepository {

    private final Map<Long, FavoriteRoute> store = new HashMap<>();
    private long nextId = 1L;

    public FavoriteRoute save(FavoriteRoute fav) {
        if (fav.getId() == null) {
            fav.setId(nextId++);
        }
        store.put(fav.getId(), fav);
        return fav;
    }

    public FavoriteRoute findById(Long id) {
        return store.get(id);
    }

    public Collection<FavoriteRoute> findAllByUserId(Long userId) {
        return store.values().stream().filter(f -> f.getUser() != null && f.getUser().getId().equals(userId)).collect(Collectors.toList());
    }

    public void deleteById(Long id) {
        store.remove(id);
    }
}