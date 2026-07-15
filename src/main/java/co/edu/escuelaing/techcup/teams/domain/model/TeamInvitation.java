package co.edu.escuelaing.techcup.teams.domain.model;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamInvitation {

    private String id;
    private String teamId;
    private String teamName;
    private String invitedUserId;
    private String invitedBy;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
