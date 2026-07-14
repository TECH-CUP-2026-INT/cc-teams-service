package co.edu.escuelaing.techcup.teams.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request payload for inviting a player to a team.
 */
public class InvitePlayerRequest {

    @NotBlank(message = "Invited player email is required")
    @Email(message = "Must be a valid email")
    private String invitedEmail;

    public String getInvitedEmail() { return invitedEmail; }
    public void setInvitedEmail(String invitedEmail) { this.invitedEmail = invitedEmail; }
}
