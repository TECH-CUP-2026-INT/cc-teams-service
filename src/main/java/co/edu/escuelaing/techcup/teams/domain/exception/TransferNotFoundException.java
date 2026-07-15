package co.edu.escuelaing.techcup.teams.domain.exception;

public class TransferNotFoundException extends DomainException {

    public TransferNotFoundException(String transferId) {
        super("TRANSFER_NOT_FOUND", "Captaincy transfer request not found: " + transferId);
    }
}
