package co.edu.escuelaing.techcup.teams.domain.port.out;

import java.util.UUID;

public interface NotificationPort {

    void publishTeamInvitation(UUID teamId, String teamName, UUID invitedUserId,
                               UUID invitationId, UUID invitedBy);

    void publishCaptaincyTransfer(UUID teamId, String teamName,
                                  UUID currentCaptainId, UUID newCaptainId, String initiatedBy);

    void publishTeamLinkRequest(UUID teamId, String teamName, UUID requesterId,
                                String requesterName, UUID recipientId, UUID requestId);

    void publishTeamLinkResponse(UUID teamId, String teamName, UUID requestId,
                                 UUID recipientId, boolean accepted);
}
