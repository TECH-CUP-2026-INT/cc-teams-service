package co.edu.escuelaing.techcup.teams.domain.exception;

public class InvalidTransferStateException extends DomainException {

    public InvalidTransferStateException(String message) {
        super("INVALID_TRANSFER_STATE", message);
    }
}
