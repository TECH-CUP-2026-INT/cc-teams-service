package co.edu.escuelaing.techcup.teams.dto;

import co.edu.escuelaing.techcup.teams.entity.InvitationStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for a player responding to an invitation.
 * Only {@code ACCEPTED} and {@code REJECTED} are valid responses.
 */
public class RespondInvitationRequest {

    @NotNull(message = "Status is required")
    private InvitationStatus status;

    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }
}
