package co.edu.escuelaing.techcup.teams.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for captaincy transfer.
 * The current Captain provides the email of the player who will become the new Captain.
 */
public class TransferCaptainRequest {

    @NotBlank(message = "New captain email is required")
    @Email(message = "Must be a valid email")
    private String newCaptainEmail;

    public TransferCaptainRequest() {}

    public String getNewCaptainEmail() { return newCaptainEmail; }
    public void setNewCaptainEmail(String newCaptainEmail) { this.newCaptainEmail = newCaptainEmail; }
}
