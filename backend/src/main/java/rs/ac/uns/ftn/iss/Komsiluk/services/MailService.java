package rs.ac.uns.ftn.iss.Komsiluk.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationMail(String to, String token) {
        // DEV shortcut: svi mejlovi idu na test inbox
        String devInbox = "komsiluk.tim@gmail.com";  // zajednički inbox
        to = devInbox;


        String activationLink = frontendUrl +
                "/activation?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Activate your Komsiluk account");
        message.setText(
                "Welcome!\n\n" +
                        "Click the link below to activate your account:\n\n" +
                        activationLink + "\n\n" +
                        "This link expires in 24 hours."
        );

        mailSender.send(message);
    }

    public void sendPasswordResetMail(String to, String token) {
        // DEV shortcut: svi mejlovi idu na test inbox
        String devInbox = "komsiluk.tim@gmail.com";  // zajednički inbox
        to = devInbox;


        String resetLink = frontendUrl +
                "/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your Komsiluk password");
        message.setText(
                "You requested a password reset.\n\n" +
                        "Click the link below to set a new password:\n\n" +
                        resetLink + "\n\n" +
                        "This link expires in 24 hours."
        );

        mailSender.send(message);
    }

}

