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

    public void sendLinkedPassengerAddedMail(String to, Long rideId) {

        // TODO: add tracking link later (once the ride tracking page is implemented)
        // String trackingLink = "http://localhost:4200/ride-live?rideId=" + rideId;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("You were added to a ride");
        message.setText(
                        "You have been added as a linked passenger to a ride.\n" +
                        "A suitable driver has been found and the ride has been accepted.\n\n" +
                        "Ride tracking will be available in the app (a link will be added later).\n\n" +
                        "Thank you!\n" +
                        "Komsiluk Taxi"
        );

        mailSender.send(message);
    }




}

