package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import rs.ac.uns.ftn.iss.Komsiluk.beans.Ride;
import rs.ac.uns.ftn.iss.Komsiluk.beans.RideReminder;
import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.NotificationType;
import rs.ac.uns.ftn.iss.Komsiluk.beans.enums.RideStatus;
import rs.ac.uns.ftn.iss.Komsiluk.dtos.notification.NotificationCreateDTO;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideReminderRepository;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.RideRepository;

@Service
public class RideReminderService {

	@Autowired
    private RideRepository rideRepository;
	@Autowired
    private RideReminderRepository reminderRepository;
	@Autowired
    private UserService userService;
	@Autowired
    private NotificationService notificationService;

    @Scheduled(fixedDelay = 30_000)
    public void tick() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus15 = now.plusMinutes(15);

        List<Ride> soon = rideRepository.findScheduledStartingSoon(RideStatus.SCHEDULED, now, nowPlus15);

        for (Ride ride : soon) {
            createSlotIfMissing(ride, ride.getStartTime().minusMinutes(15));
            createSlotIfMissing(ride, ride.getStartTime().minusMinutes(10));
            createSlotIfMissing(ride, ride.getStartTime().minusMinutes(5));
        }

        List<RideReminder> due = reminderRepository.findBySentFalseAndSlotTimeLessThanEqual(now);

        for (RideReminder rideReminder : due) {
            Ride ride = rideRepository.findById(rideReminder.getRideId()).orElse(null);
            if (ride == null) {
                markSent(rideReminder, now);
                continue;
            }

            if (ride.getStartTime() == null || !RideStatus.SCHEDULED.equals(ride.getStatus())) {
                markSent(rideReminder, now);
                continue;
            }
            if (!ride.getStartTime().isAfter(now)) {
                markSent(rideReminder, now);
                continue;
            }

            User user = userService.findById(rideReminder.getUserId());
            long mins = Duration.between(now, ride.getStartTime()).toMinutes();
            
            NotificationCreateDTO dto = new NotificationCreateDTO();
            dto.setUserId(user.getId());
            dto.setType(NotificationType.INFO);
            dto.setTitle("Ride Reminder");
            dto.setMessage("Your scheduled ride starts in " + mins + " minutes.");
            notificationService.createNotification(dto);

            markSent(rideReminder, now);
        }
    }

    private void createSlotIfMissing(Ride r, LocalDateTime slot) {
        if (slot == null) return;

        LocalDateTime now = LocalDateTime.now();
        if (slot.isBefore(now.minusMinutes(1))) return;

        Long rideId = r.getId();
        Long userId = r.getCreatedBy().getId();

        if (reminderRepository.existsByRideIdAndUserIdAndSlotTime(rideId, userId, slot)) return;

        RideReminder rr = new RideReminder();
        rr.setRideId(rideId);
        rr.setUserId(userId);
        rr.setSlotTime(slot);
        reminderRepository.save(rr);
    }

    private void markSent(RideReminder rr, LocalDateTime now) {
        rr.setSent(true);
        rr.setSentAt(now);
        reminderRepository.save(rr);
    }
}
