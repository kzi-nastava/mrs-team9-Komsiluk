package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Resource already exists")
public class AlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

}
