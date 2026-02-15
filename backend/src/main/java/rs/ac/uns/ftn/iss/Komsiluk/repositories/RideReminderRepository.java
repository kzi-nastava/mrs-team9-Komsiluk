package rs.ac.uns.ftn.iss.Komsiluk.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import rs.ac.uns.ftn.iss.Komsiluk.beans.RideReminder;

@Repository
public interface RideReminderRepository extends JpaRepository<RideReminder, Long> {

    List<RideReminder> findBySentFalseAndSlotTimeLessThanEqual(LocalDateTime now);

    boolean existsByRideIdAndUserIdAndSlotTime(Long rideId, Long userId, LocalDateTime slotTime);
}