package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

	public Collection<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    public Collection<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

}