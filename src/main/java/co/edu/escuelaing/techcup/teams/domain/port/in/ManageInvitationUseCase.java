package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;

import java.util.List;
import java.util.UUID;

public interface ManageInvitationUseCase {

    TeamInvitation sendInvitation(UUID captainId, UUID teamId, UUID invitedUserId);

    TeamInvitation respondToInvitation(UUID userId, String userName, UUID invitationId, boolean accept);

    List<TeamInvitation> getInvitationsForUser(UUID userId);

    List<TeamInvitation> getInvitationsForTeam(UUID captainId, UUID teamId);
}
