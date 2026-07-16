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
import java.util.UUID;

public final class TestFixtures {

    public static final UUID CAPTAIN_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final String CAPTAIN_NAME = "Ada Lovelace";
    public static final UUID PLAYER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final String PLAYER_NAME = "Grace Hopper";
    public static final UUID TEAM_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final String TEAM_NAME = "Los Halcones FC";
    public static final UUID INVITATION_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    public static final UUID TRANSFER_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    public static final UUID NEW_PLAYER_ID = UUID.fromString("66666666-6666-6666-6666-666666666666");
    public static final UUID OTHER_USER_ID = UUID.fromString("77777777-7777-7777-7777-777777777777");
    public static final UUID NON_MEMBER_ID = UUID.fromString("88888888-8888-8888-8888-888888888888");
    public static final UUID NONEXISTENT_TEAM_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");
    public static final UUID AUDIT_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

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

    public static Team teamEligibleForTournament() {
        List<TeamMember> members = new ArrayList<>(List.of(captainMember(), playerMember()));
        for (int i = 0; i < 5; i++) {
            members.add(TeamMember.builder()
                    .userId(UUID.randomUUID())
                    .fullName("Player " + i)
                    .role(TeamMemberRole.PLAYER)
                    .joinedAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                    .build());
        }
        return Team.builder()
                .id(TEAM_ID)
                .name(TEAM_NAME)
                .logo(new byte[]{1, 2, 3})
                .logoContentType("image/png")
                .colors("#FF0000,#FFFFFF")
                .captainId(CAPTAIN_ID)
                .members(members)
                .createdAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(5))
                .updatedAt(LocalDateTime.now(ZoneOffset.UTC).minusDays(1))
                .build();
    }

    public static TeamInvitation pendingInvitation() {
        return TeamInvitation.builder()
                .id(INVITATION_ID)
                .teamId(TEAM_ID)
                .teamName(TEAM_NAME)
                .invitedUserId(NEW_PLAYER_ID)
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
                .id(AUDIT_ID)
                .teamId(TEAM_ID)
                .userId(CAPTAIN_ID)
                .actionType(AuditActionType.TEAM_CREATED)
                .description("Team created")
                .success(true)
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build();
    }
}
