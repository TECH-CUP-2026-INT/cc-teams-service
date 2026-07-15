package co.edu.escuelaing.techcup.teams.support;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.enums.TeamMemberRole;
import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.domain.model.TeamMember;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public final class TestFixtures {

    public static final String CAPTAIN_ID = "captain-1";
    public static final String CAPTAIN_NAME = "Ada Lovelace";
    public static final String PLAYER_ID = "player-1";
    public static final String PLAYER_NAME = "Grace Hopper";
    public static final String TEAM_ID = "team-1";
    public static final String TEAM_NAME = "Los Halcones FC";
    public static final String INVITATION_ID = "inv-1";
    public static final String TRANSFER_ID = "transfer-1";

    private TestFixtures() {
    }

    public static TeamMember captainMember() {
        return TeamMember.builder()
                .userId(CAPTAIN_ID)
                .fullName(CAPTAIN_NAME)
                .role(TeamMemberRole.CAPTAIN)
                .joinedAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(5))
                .build();
    }

    public static TeamMember playerMember() {
        return TeamMember.builder()
                .userId(PLAYER_ID)
                .fullName(PLAYER_NAME)
                .role(TeamMemberRole.PLAYER)
                .joinedAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(3))
                .build();
    }

    public static Team team() {
        return Team.builder()
                .id(TEAM_ID)
                .name(TEAM_NAME)
                .logo(new byte[]{1, 2, 3})
                .logoContentType("image/png")
                .colors("#FF0000,#FFFFFF")
                .captainId(CAPTAIN_ID)
                .members(new ArrayList<>(List.of(captainMember(), playerMember())))
                .createdAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(5))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
    }

    public static Team teamWithOnlyCaptain() {
        return Team.builder()
                .id(TEAM_ID)
                .name(TEAM_NAME)
                .logo(new byte[]{1, 2, 3})
                .logoContentType("image/png")
                .captainId(CAPTAIN_ID)
                .members(new ArrayList<>(List.of(captainMember())))
                .createdAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(5))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
    }

    public static TeamInvitation pendingInvitation() {
        return TeamInvitation.builder()
                .id(INVITATION_ID)
                .teamId(TEAM_ID)
                .teamName(TEAM_NAME)
                .invitedUserId("new-player-1")
                .invitedBy(CAPTAIN_ID)
                .status(InvitationStatus.PENDING)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC).minusHours(1))
                .build();
    }

    public static CaptaincyTransferRequest pendingTransfer() {
        return CaptaincyTransferRequest.builder()
                .id(TRANSFER_ID)
                .teamId(TEAM_ID)
                .teamName(TEAM_NAME)
                .currentCaptainId(CAPTAIN_ID)
                .newCaptainId(PLAYER_ID)
                .initiatedBy("CAPTAIN")
                .status(TransferRequestStatus.PENDING)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(30))
                .build();
    }

    public static AuditEvent auditEvent() {
        return AuditEvent.builder()
                .id("audit-1")
                .teamId(TEAM_ID)
                .userId(CAPTAIN_ID)
                .actionType(AuditActionType.TEAM_CREATED)
                .description("Team created")
                .success(true)
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }
}
