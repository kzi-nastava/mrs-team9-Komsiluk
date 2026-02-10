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
        String devInbox = "komsiluk.tim@gmail.com";
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
    
    public void sendDriverActivationMail(String to, String token) {
        String devInbox = "komsiluk.tim@gmail.com";
        to = devInbox;

        String activationLink = frontendUrl + "/driver-activation?token=" + token;

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

    public void sendRideFinishedMail(String to, Long rideId) {
        String ratingLink = frontendUrl + "/login?rateRideId=" + rideId;
        String devInbox = "komsiluk.tim@gmail.com";
        to = devInbox;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Komsiluk Taxi - Ride Completed");
        message.setText(
                "Your ride has just been completed.\n" +
                        "You can rate the driver and the vehicle here:\n" +
                        ratingLink + "\n\n" +
                        "Thank you for using Komsiluk Taxi!"
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Greška pri slanju mejla na " + to + ": " + e.getMessage());
        }
    }


    public void sendAddedToRideMail(String to, Long rideId) {
        String devInbox = "komsiluk.tim@gmail.com";
        to = devInbox;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("You were added to a ride");
        message.setText(
                        "You have been added to a ride.\n\n" +
                        "You will receive another email when the ride starts.\n\n" +
                        "Thank you!\n" +
                        "Komsiluk Taxi"
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Greška pri slanju mejla na " + to + ": " + e.getMessage());
        }
    }

    public void sendRideStartedMail(String to, Long rideId) {
        String trackingLink = frontendUrl + "/login" ;
        String devInbox = "komsiluk.tim@gmail.com";
        to = devInbox;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your ride has started");
        message.setText(
                        "Your ride has just started.\n\n" +
                        "If you want to track the ride in real time, open this link:\n" +
                        trackingLink + "\n\n" +
                        "Note: Ride tracking is available only for registered (logged-in) users.\n\n" +
                        "Thank you!\n" +
                        "Komsiluk Taxi"
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Greška pri slanju mejla na " + to + ": " + e.getMessage());
        }
    }





}

