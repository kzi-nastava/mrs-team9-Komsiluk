package rs.ac.uns.ftn.iss.Komsiluk.socket.config;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

  @PostConstruct
  public void init() throws IOException {
    if (!FirebaseApp.getApps().isEmpty()) return;

    InputStream in = getClass().getClassLoader().getResourceAsStream("firebase/serviceAccountKey.json");
    FirebaseOptions options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.fromStream(in))
        .build();
    FirebaseApp.initializeApp(options);
  }
}

