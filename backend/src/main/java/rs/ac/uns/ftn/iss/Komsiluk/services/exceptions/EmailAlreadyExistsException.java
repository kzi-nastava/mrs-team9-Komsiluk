package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("User with email '" + email + "' already exists.");
    }
}
