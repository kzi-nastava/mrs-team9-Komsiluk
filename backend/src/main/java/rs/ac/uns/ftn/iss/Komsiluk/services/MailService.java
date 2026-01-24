package rs.ac.uns.ftn.iss.Komsiluk.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendActivationMail(String to, String token) {

        String activationLink =
                "http://localhost:4200/activation?token=" + token;

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

        String activationLink =
                "http://localhost:4200/driver-activation?token=" + token;

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

        String resetLink =
                "http://localhost:4200/reset-password?token=" + token;

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
        // TODO: replace with real frontend link (this current one looks like a placeholder/typo)
        String ratingLink = "http://localhost:4200=" + rideId;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Ride completed");
        message.setText(
                        "Your ride has just been completed.\n" +
                        "You can rate the driver and the vehicle and leave a comment.\n\n" +
                        "Rating link:\n" +
                        ratingLink + "\n\n" +
                        "You have 3 days from the ride completion time to submit your rating.\n\n" +
                        "Thank you for using Komsiluk Taxi!"
        );

        mailSender.send(message);
    }

    public void sendAddedToRideMail(String to, Long rideId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("You were added to a ride");
        message.setText(
                        "You have been added to a ride.\n\n" +
                        "You will receive another email when the ride starts.\n\n" +
                        "Thank you!\n" +
                        "Komsiluk Taxi"
        );

        mailSender.send(message);
    }

    public void sendRideStartedMail(String to, Long rideId) {
        // TODO: replace with real tracking link once the frontend route exists
        String trackingLink = "http://localhost:4200/ride-live?rideId=" + rideId;

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

        mailSender.send(message);
    }





}

