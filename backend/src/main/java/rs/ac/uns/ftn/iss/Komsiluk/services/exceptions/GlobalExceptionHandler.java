package rs.ac.uns.ftn.iss.Komsiluk.services.exceptions;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> handleInvalidPassword(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Password is incorrect"));
    }
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException ex) {
        String msg = (ex.getMessage() != null && !ex.getMessage().isBlank()) ? ex.getMessage() : "Resource not found";
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", msg));
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage()));
    }
    
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<?> handleAlreadyExistsException(AlreadyExistsException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
	}

    @ExceptionHandler(ActivationAlreadySentException.class)
    public ResponseEntity<?> handleActivationAlreadySent(
            ActivationAlreadySentException ex) {

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of("message", ex.getMessage()));
    }
}
