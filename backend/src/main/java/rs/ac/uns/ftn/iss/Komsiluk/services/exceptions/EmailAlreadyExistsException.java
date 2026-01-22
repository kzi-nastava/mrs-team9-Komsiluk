package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailAlreadyExistsException(String email) {
        super("User with email '" + email + "' already exists.");
    }
}
