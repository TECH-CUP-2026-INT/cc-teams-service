package co.edu.escuelaing.techcup.teams.exception;

/**
 * Thrown when a business rule is violated (e.g. duplicate team name).
 * Maps to HTTP 400 via {@link GlobalExceptionHandler}.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
}
