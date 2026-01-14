package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;


public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException() {
        super("Invalid email or password.");
    }
}


