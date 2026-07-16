package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class TransferNotFoundException extends DomainException {

    public TransferNotFoundException(UUID transferId) {
        super("TRANSFER_NOT_FOUND", "Captaincy transfer request not found: " + transferId);
    }
}
