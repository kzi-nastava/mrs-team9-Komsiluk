package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "Unauthorized access")
public class UnauthorizedAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UnauthorizedAccessException() {
        super();
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
