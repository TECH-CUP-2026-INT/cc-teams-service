package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.notification;

import co.edu.escuelaing.techcup.teams.domain.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Stub adapter for notification events.
 * Will be replaced with actual REST/messaging integration to Communications Service.
 */
@Slf4j
@Component
public class NotificationAdapter implements NotificationPort {

    @Override
    public void publishTeamInvitation(UUID teamId, String teamName, UUID invitedUserId,
                                      UUID invitationId, UUID invitedBy) {
        log.info("TeamInvitationEvent: teamId={}, teamName={}, invitedUserId={}, invitationId={}, invitedBy={}",
                teamId, teamName, invitedUserId, invitationId, invitedBy);
    }

    @Override
    public void publishCaptaincyTransfer(UUID teamId, String teamName,
                                         UUID currentCaptainId, UUID newCaptainId, String initiatedBy) {
        log.info("CaptaincyTransferEvent: teamId={}, teamName={}, currentCaptain={}, newCaptain={}, initiatedBy={}",
                teamId, teamName, currentCaptainId, newCaptainId, initiatedBy);
    }

    @Override
    public void publishTeamLinkRequest(UUID teamId, String teamName, UUID requesterId,
                                       String requesterName, UUID recipientId, UUID requestId) {
        log.info("TeamLinkRequestEvent: teamId={}, teamName={}, requesterId={}, recipientId={}, requestId={}",
                teamId, teamName, requesterId, recipientId, requestId);
    }

    @Override
    public void publishTeamLinkResponse(UUID teamId, String teamName, UUID requestId,
                                        UUID recipientId, boolean accepted) {
        log.info("TeamLinkResponseEvent: teamId={}, teamName={}, requestId={}, recipientId={}, accepted={}",
                teamId, teamName, requestId, recipientId, accepted);
    }
}
