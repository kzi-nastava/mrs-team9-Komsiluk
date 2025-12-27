package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Notification;

@Repository
public class NotificationRepository {

    private final Map<Long, Notification> storage = new HashMap<>();
    private long nextId = 1L;

    public Notification save(Notification n) {
        if (n.getId() == null) {
            n.setId(nextId++);
        }
        storage.put(n.getId(), n);
        return n;
    }

    public Optional<Notification> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Collection<Notification> findByUserId(Long userId) {
        return storage.values().stream().filter(n -> Objects.equals(n.getUser().getId(), userId)).sorted(Comparator.comparing(Notification::getCreatedAt).reversed()).collect(Collectors.toList());
    }

    public Collection<Notification> findUnreadByUserId(Long userId) {
        return storage.values().stream().filter(n -> Objects.equals(n.getUser().getId(), userId) && !n.isRead()).sorted(Comparator.comparing(Notification::getCreatedAt).reversed()).collect(Collectors.toList());
    }
}