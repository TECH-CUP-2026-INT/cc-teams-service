package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.enums.TeamMemberRole;
import co.edu.escuelaing.techcup.teams.domain.exception.InvalidInvitationStateException;
import co.edu.escuelaing.techcup.teams.domain.exception.InvitationNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.PlayerAlreadyInTeamException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamFullException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.UnauthorizedTeamActionException;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.domain.model.TeamMember;
import co.edu.escuelaing.techcup.teams.domain.port.in.ManageInvitationUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.NotificationPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamInvitationRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageInvitationUseCaseImpl implements ManageInvitationUseCase {

    private final TeamRepositoryPort teamRepository;
    private final TeamInvitationRepositoryPort invitationRepository;
    private final AuditEventRepositoryPort auditRepository;
    private final NotificationPort notificationPort;

    @Override
    public TeamInvitation sendInvitation(String captainId, String teamId, String invitedUserId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        if (!team.getCaptainId().equals(captainId)) {
            throw new UnauthorizedTeamActionException("Only the captain can send invitations");
        }

        if (team.hasMember(invitedUserId)) {
            throw new PlayerAlreadyInTeamException(invitedUserId);
        }

        if (team.isFull()) {
            throw new TeamFullException(teamId);
        }

        invitationRepository.findByTeamIdAndInvitedUserIdAndStatus(teamId, invitedUserId, InvitationStatus.PENDING)
                .ifPresent(existing -> {
                    throw new InvalidInvitationStateException("A pending invitation already exists for this player");
                });

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        TeamInvitation invitation = TeamInvitation.builder()
                .teamId(teamId)
                .teamName(team.getName())
                .invitedUserId(invitedUserId)
                .invitedBy(captainId)
                .status(InvitationStatus.PENDING)
                .createdAt(now)
                .build();

        TeamInvitation saved = invitationRepository.save(invitation);

        notificationPort.publishTeamInvitation(teamId, team.getName(), invitedUserId, saved.getId(), captainId);

        auditRepository.save(AuditEvent.builder()
                .teamId(teamId)
                .userId(captainId)
                .actionType(AuditActionType.INVITATION_SENT)
                .description("Invitation sent to player " + invitedUserId + " for team '" + team.getName() + "'")
                .success(true)
                .timestamp(now)
                .build());

        return saved;
    }

    @Override
    public TeamInvitation respondToInvitation(String userId, String userName, String invitationId, boolean accept) {
        TeamInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new InvitationNotFoundException(invitationId));

        if (!invitation.getInvitedUserId().equals(userId)) {
            throw new UnauthorizedTeamActionException("Only the invited player can respond to this invitation");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new InvalidInvitationStateException("Invitation is no longer pending");
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        invitation.setRespondedAt(now);

        if (accept) {
            Team team = teamRepository.findById(invitation.getTeamId())
                    .orElseThrow(() -> new TeamNotFoundException(invitation.getTeamId()));

            if (team.isFull()) {
                throw new TeamFullException(invitation.getTeamId());
            }

            teamRepository.findByMembersUserId(userId).ifPresent(existingTeam -> {
                throw new PlayerAlreadyInTeamException(userId);
            });

            invitation.setStatus(InvitationStatus.ACCEPTED);

            TeamMember newMember = TeamMember.builder()
                    .userId(userId)
                    .fullName(userName)
                    .role(TeamMemberRole.PLAYER)
                    .joinedAt(now)
                    .build();

            team.getMembers().add(newMember);
            team.setUpdatedAt(now);
            teamRepository.save(team);

            auditRepository.save(AuditEvent.builder()
                    .teamId(invitation.getTeamId())
                    .userId(userId)
                    .actionType(AuditActionType.INVITATION_ACCEPTED)
                    .description("Player " + userId + " accepted invitation to team '" + invitation.getTeamName() + "'")
                    .success(true)
                    .timestamp(now)
                    .build());
        } else {
            invitation.setStatus(InvitationStatus.REJECTED);

            auditRepository.save(AuditEvent.builder()
                    .teamId(invitation.getTeamId())
                    .userId(userId)
                    .actionType(AuditActionType.INVITATION_REJECTED)
                    .description("Player " + userId + " rejected invitation to team '" + invitation.getTeamName() + "'")
                    .success(true)
                    .timestamp(now)
                    .build());
        }

        return invitationRepository.save(invitation);
    }

    @Override
    public List<TeamInvitation> getInvitationsForUser(String userId) {
        return invitationRepository.findByInvitedUserId(userId);
    }

    @Override
    public List<TeamInvitation> getInvitationsForTeam(String captainId, String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        if (!team.getCaptainId().equals(captainId)) {
            throw new UnauthorizedTeamActionException("Only the captain can view team invitations");
        }

        return invitationRepository.findByTeamId(teamId);
    }
}
