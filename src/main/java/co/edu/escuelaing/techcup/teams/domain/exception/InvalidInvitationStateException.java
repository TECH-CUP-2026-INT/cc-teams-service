package co.edu.escuelaing.techcup.teams.domain.exception;

public class InvalidInvitationStateException extends DomainException {

    public InvalidInvitationStateException(String message) {
        super("INVALID_INVITATION_STATE", message);
    }
}
