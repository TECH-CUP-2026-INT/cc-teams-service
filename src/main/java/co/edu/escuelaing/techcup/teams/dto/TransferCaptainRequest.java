package co.edu.escuelaing.techcup.teams.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for captaincy transfer.
 * The current Captain provides the email of the player who will become the new Captain.
 */
@Schema(description = "Request body for transferring team captaincy")
public class TransferCaptainRequest {

    @Schema(description = "Email of the active team member who will become the new captain",
            example = "player@techcup.co", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "New captain email is required")
    @Email(message = "Must be a valid email")
    private String newCaptainEmail;

    public TransferCaptainRequest() {}

    public String getNewCaptainEmail() { return newCaptainEmail; }
    public void setNewCaptainEmail(String newCaptainEmail) { this.newCaptainEmail = newCaptainEmail; }
}
