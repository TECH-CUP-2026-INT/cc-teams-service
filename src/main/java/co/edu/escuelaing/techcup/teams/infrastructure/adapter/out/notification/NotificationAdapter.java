package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.notification;

import co.edu.escuelaing.techcup.teams.domain.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Stub adapter for notification events.
 * Will be replaced with actual REST/messaging integration to Communications Service.
 */
@Slf4j
@Component
public class NotificationAdapter implements NotificationPort {

    @Override
    public void publishTeamInvitation(String teamId, String teamName, String invitedUserId,
                                      String invitationId, String invitedBy) {
        log.info("TeamInvitationEvent: teamId={}, teamName={}, invitedUserId={}, invitationId={}, invitedBy={}",
                teamId, teamName, invitedUserId, invitationId, invitedBy);
    }

    @Override
    public void publishCaptaincyTransfer(String teamId, String teamName,
                                         String currentCaptainId, String newCaptainId, String initiatedBy) {
        log.info("CaptaincyTransferEvent: teamId={}, teamName={}, currentCaptain={}, newCaptain={}, initiatedBy={}",
                teamId, teamName, currentCaptainId, newCaptainId, initiatedBy);
    }

    @Override
    public void publishTeamLinkRequest(String teamId, String teamName, String requesterId,
                                       String requesterName, String recipientId, String requestId) {
        log.info("TeamLinkRequestEvent: teamId={}, teamName={}, requesterId={}, recipientId={}, requestId={}",
                teamId, teamName, requesterId, recipientId, requestId);
    }

    @Override
    public void publishTeamLinkResponse(String teamId, String teamName, String requestId,
                                        String recipientId, boolean accepted) {
        log.info("TeamLinkResponseEvent: teamId={}, teamName={}, requestId={}, recipientId={}, accepted={}",
                teamId, teamName, requestId, recipientId, accepted);
    }
}
