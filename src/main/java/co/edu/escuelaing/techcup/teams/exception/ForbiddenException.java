package co.edu.escuelaing.techcup.teams.exception;

/**
 * Thrown when the authenticated user attempts an action they are not authorized to perform.
 * Maps to HTTP 403 via {@link GlobalExceptionHandler}.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) { super(message); }
}
