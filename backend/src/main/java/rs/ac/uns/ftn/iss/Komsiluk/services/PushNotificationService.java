package rs.ac.uns.ftn.iss.Komsiluk.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;
import rs.ac.uns.ftn.iss.Komsiluk.beans.UserFcmToken;
import rs.ac.uns.ftn.iss.Komsiluk.repositories.UserFcmTokenRepository;

@Service
public class PushNotificationService {

    private final UserFcmTokenRepository repo;

    public PushNotificationService(UserFcmTokenRepository repo) {
        this.repo = repo;
    }

    public void registerToken(User user, String token) {
        var existing = repo.findByToken(token);
        if (existing.isPresent()) {
            UserFcmToken tok = existing.get();
            
            if(!tok.getUser().getId().equals(user.getId())) {
				tok.setUser(user);
			}
            
            tok.setLastSeenAt(LocalDateTime.now());
            repo.save(tok);
            return;
        }

        repo.save(new UserFcmToken(user, token));
    }

    public void sendToUser(Long userId, String title, String message, Map<String, String> data) {
        List<String> tokens = repo.findTokensByUserId(userId);
        for (String token : tokens) {
            sendToToken(token, title, message, data);
        }
    }

    private void sendToToken(String token, String title, String message, Map<String, String> data) {
        Message.Builder b = Message.builder()
                .setToken(token)
                .putData("title", title)
                .putData("message", message);

        if (data != null) b.putAllData(data);

        try {
            FirebaseMessaging.getInstance().send(b.build());
        } catch (Exception e) {
            repo.deleteByToken(token);
        }
    }
}
