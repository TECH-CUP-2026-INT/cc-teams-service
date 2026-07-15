package co.edu.escuelaing.techcup.teams.domain.port.out;

public interface NotificationPort {

    void publishTeamInvitation(String teamId, String teamName, String invitedUserId,
                               String invitationId, String invitedBy);

    void publishCaptaincyTransfer(String teamId, String teamName,
                                  String currentCaptainId, String newCaptainId, String initiatedBy);

    void publishTeamLinkRequest(String teamId, String teamName, String requesterId,
                                String requesterName, String recipientId, String requestId);

    void publishTeamLinkResponse(String teamId, String teamName, String requestId,
                                 String recipientId, boolean accepted);
}
