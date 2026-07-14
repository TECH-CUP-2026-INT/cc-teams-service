package co.edu.escuelaing.techcup.teams.dto;

import co.edu.escuelaing.techcup.teams.entity.TeamEntity;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO representing a team. Never exposes the JPA entity directly.
 */
public class TeamResponse {

    private UUID id;
    private String name;
    private String logo;
    private String captainEmail;
    private LocalDateTime createdAt;

    public static TeamResponse from(TeamEntity entity) {
        TeamResponse dto = new TeamResponse();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.logo = entity.getLogo();
        dto.captainEmail = entity.getCaptainEmail();
        dto.createdAt = entity.getCreatedAt();
        return dto;
    }

    public UUID getId()                 { return id; }
    public String getName()             { return name; }
    public String getLogo()             { return logo; }
    public String getCaptainEmail()     { return captainEmail; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
