package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class ActivationAlreadySentException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ActivationAlreadySentException() {
        super("Activation email already sent. Please check your inbox.");
    }
}

