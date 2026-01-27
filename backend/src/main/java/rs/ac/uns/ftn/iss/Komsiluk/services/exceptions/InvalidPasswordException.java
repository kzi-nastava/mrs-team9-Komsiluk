package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password is incorrect")
public class InvalidPasswordException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public InvalidPasswordException() {
		super();
	}
    
    public InvalidPasswordException(String message) {
		super(message);
	}
}