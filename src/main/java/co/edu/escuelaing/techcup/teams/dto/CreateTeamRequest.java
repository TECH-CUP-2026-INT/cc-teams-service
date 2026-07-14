package co.edu.escuelaing.techcup.teams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request payload for creating a new team.
 */
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    @Size(max = 100, message = "Team name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Logo URL is required")
    @Size(max = 500, message = "Logo URL must not exceed 500 characters")
    private String logo;

    public String getName() { return name; }
    public String getLogo() { return logo; }
    public void setName(String name) { this.name = name; }
    public void setLogo(String logo) { this.logo = logo; }
}
