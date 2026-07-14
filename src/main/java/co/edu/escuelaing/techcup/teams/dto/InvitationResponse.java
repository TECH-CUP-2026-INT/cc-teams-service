package co.edu.escuelaing.techcup.teams.dto;

import co.edu.escuelaing.techcup.teams.entity.InvitationEntity;
import co.edu.escuelaing.techcup.teams.entity.InvitationStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for an invitation. Never exposes the JPA entity directly.
 */
public class InvitationResponse {

    private UUID id;
    private UUID teamId;
    private String invitedEmail;
    private InvitationStatus status;
    private LocalDateTime createdAt;

    public static InvitationResponse from(InvitationEntity entity) {
        InvitationResponse dto = new InvitationResponse();
        dto.id = entity.getId();
        dto.teamId = entity.getTeamId();
        dto.invitedEmail = entity.getInvitedEmail();
        dto.status = entity.getStatus();
        dto.createdAt = entity.getCreatedAt();
        return dto;
    }

    public UUID getId()                 { return id; }
    public UUID getTeamId()             { return teamId; }
    public String getInvitedEmail()     { return invitedEmail; }
    public InvitationStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
