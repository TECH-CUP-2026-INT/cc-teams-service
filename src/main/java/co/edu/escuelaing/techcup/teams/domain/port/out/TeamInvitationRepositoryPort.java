package co.edu.escuelaing.techcup.teams.domain.port.out;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;

import java.util.List;
import java.util.Optional;

public interface TeamInvitationRepositoryPort {

    TeamInvitation save(TeamInvitation invitation);

    Optional<TeamInvitation> findById(String id);

    List<TeamInvitation> findByInvitedUserId(String userId);

    List<TeamInvitation> findByTeamId(String teamId);

    Optional<TeamInvitation> findByTeamIdAndInvitedUserIdAndStatus(String teamId, String userId, InvitationStatus status);
}
