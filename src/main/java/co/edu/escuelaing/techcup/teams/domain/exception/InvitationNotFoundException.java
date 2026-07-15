package co.edu.escuelaing.techcup.teams.domain.exception;

public class InvitationNotFoundException extends DomainException {

    public InvitationNotFoundException(String invitationId) {
        super("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId);
    }
}
