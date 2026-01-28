package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad Request")
public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		super();
	}
	
	public BadRequestException(String message) {
		super(message);
	}
}
