package uni.projects.backend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
@ControllerAdvice(annotations = RestController.class)
public class ControllerExceptionHelper {

    private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHelper.class);

    /**
     * Handles InvalidFileException.
     * Use this exception when a provided file is invalid (e.g., incorrect format, corrupted).
     * Returns HTTP 422 UNPROCESSABLE_ENTITY.
     */
    @ExceptionHandler(value = { InvalidFileException.class })
    public ResponseEntity<ExceptionDetails> handleInvalidFile(InvalidFileException ex) {
        log.error("Invalid File Exception: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    /**
     * Handles ResourceNotFoundException.
     * Use this exception when a requested resource does not exist.
     * Returns HTTP 404 NOT_FOUND.
     */
    @ExceptionHandler(value = { ResourceNotFoundException.class })
    public ResponseEntity<ExceptionDetails> handleResourceNotFound(ResourceNotFoundException ex) {
        log.error("Resource Not Found Exception: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles UnauthorizedException.
     * Use this exception when a user is not authorized to perform an action.
     * Returns HTTP 401 UNAUTHORIZED.
     */
    @ExceptionHandler(value = { UnauthorizedException.class })
    public ResponseEntity<ExceptionDetails> handleUnauthorized(UnauthorizedException ex) {
        log.error("Unauthorized Exception: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    /**
     * Handles UserValidationException.
     * Use this exception when user input fails validation (e.g., invalid email, missing fields).
     * Returns HTTP 403 FORBIDDEN.
     */
    @ExceptionHandler(value = { UserValidationException.class })
    public ResponseEntity<ExceptionDetails> handleUserValidation(UserValidationException ex) {
        log.error("User Validation Exception: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    /**
     * Handles ArgumentException.
     * Use this exception when arguments passed to a method are incorrect or missing.
     * Returns HTTP 412 PRECONDITION_FAILED.
     */
    @ExceptionHandler(value = { ArgumentException.class })
    public ResponseEntity<ExceptionDetails> handleArgumentException(ArgumentException ex) {
        log.error("Argument Exception: {}", ex.getMessage());
        return buildResponseEntity(HttpStatus.PRECONDITION_FAILED, ex.getMessage());
    }

    // Generic method to build a response entity
    private ResponseEntity<ExceptionDetails> buildResponseEntity(HttpStatus status, String message) {
        ExceptionDetails exceptionDetails = new ExceptionDetails(status, message);
        return new ResponseEntity<>(exceptionDetails, status);
    }
}

