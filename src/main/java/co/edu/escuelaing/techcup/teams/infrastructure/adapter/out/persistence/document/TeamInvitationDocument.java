package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "team_invitations")
public class TeamInvitationDocument {

    @Id
    private UUID id;

    @Indexed
    private UUID teamId;
    private String teamName;

    @Indexed
    private UUID invitedUserId;
    private UUID invitedBy;
    private InvitationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
