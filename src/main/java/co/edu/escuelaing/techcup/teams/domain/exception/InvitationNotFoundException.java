package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class InvitationNotFoundException extends DomainException {

    public InvitationNotFoundException(UUID invitationId) {
        super("INVITATION_NOT_FOUND", "Invitation not found: " + invitationId);
    }
}
