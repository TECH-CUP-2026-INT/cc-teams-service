package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;

import java.util.List;

public interface ManageInvitationUseCase {

    TeamInvitation sendInvitation(String captainId, String teamId, String invitedUserId);

    TeamInvitation respondToInvitation(String userId, String userName, String invitationId, boolean accept);

    List<TeamInvitation> getInvitationsForUser(String userId);

    List<TeamInvitation> getInvitationsForTeam(String captainId, String teamId);
}
