package co.edu.escuelaing.techcup.teams.domain.port.out;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamInvitationRepositoryPort {

    TeamInvitation save(TeamInvitation invitation);

    Optional<TeamInvitation> findById(UUID id);

    List<TeamInvitation> findByInvitedUserId(UUID userId);

    List<TeamInvitation> findByTeamId(UUID teamId);

    Optional<TeamInvitation> findByTeamIdAndInvitedUserIdAndStatus(UUID teamId, UUID userId, InvitationStatus status);
}
