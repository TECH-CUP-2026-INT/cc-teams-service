package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.enums.TeamMemberRole;
import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.exception.InvalidTransferStateException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.TransferNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.UnauthorizedTeamActionException;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.model.TeamMember;
import co.edu.escuelaing.techcup.teams.domain.port.in.TransferCaptaincyUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.CaptaincyTransferRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.NotificationPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class TransferCaptaincyUseCaseImpl implements TransferCaptaincyUseCase {

    private final TeamRepositoryPort teamRepository;
    private final CaptaincyTransferRepositoryPort transferRepository;
    private final AuditEventRepositoryPort auditRepository;
    private final NotificationPort notificationPort;

    @Override
    public CaptaincyTransferRequest initiateTransfer(String captainId, String teamId, String newCaptainId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        if (!team.getCaptainId().equals(captainId)) {
            throw new UnauthorizedTeamActionException("Only the current captain can initiate a transfer");
        }

        if (!team.hasMember(newCaptainId)) {
            throw new UnauthorizedTeamActionException("Target player must be a member of the team");
        }

        validateNoPendingTransfer(teamId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        CaptaincyTransferRequest transfer = CaptaincyTransferRequest.builder()
                .teamId(teamId)
                .teamName(team.getName())
                .currentCaptainId(captainId)
                .newCaptainId(newCaptainId)
                .initiatedBy("CAPTAIN")
                .status(TransferRequestStatus.PENDING)
                .createdAt(now)
                .build();

        CaptaincyTransferRequest saved = transferRepository.save(transfer);

        notificationPort.publishCaptaincyTransfer(teamId, team.getName(), captainId, newCaptainId, "CAPTAIN");

        auditRepository.save(AuditEvent.builder()
                .teamId(teamId)
                .userId(captainId)
                .actionType(AuditActionType.CAPTAINCY_TRANSFER_INITIATED)
                .description("Captain " + captainId + " initiated transfer to player " + newCaptainId)
                .success(true)
                .timestamp(now)
                .build());

        return saved;
    }

    @Override
    public CaptaincyTransferRequest applyForCaptaincy(String playerId, String playerName, String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        if (!team.hasMember(playerId)) {
            throw new UnauthorizedTeamActionException("Player must be a member of the team");
        }

        if (team.getCaptainId().equals(playerId)) {
            throw new InvalidTransferStateException("Player is already the captain");
        }

        validateNoPendingTransfer(teamId);

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        CaptaincyTransferRequest transfer = CaptaincyTransferRequest.builder()
                .teamId(teamId)
                .teamName(team.getName())
                .currentCaptainId(team.getCaptainId())
                .newCaptainId(playerId)
                .initiatedBy("PLAYER")
                .status(TransferRequestStatus.PENDING)
                .createdAt(now)
                .build();

        CaptaincyTransferRequest saved = transferRepository.save(transfer);

        notificationPort.publishTeamLinkRequest(teamId, team.getName(), playerId,
                playerName, team.getCaptainId(), saved.getId());

        auditRepository.save(AuditEvent.builder()
                .teamId(teamId)
                .userId(playerId)
                .actionType(AuditActionType.CAPTAINCY_APPLICATION_SENT)
                .description("Player " + playerId + " applied for captaincy of team '" + team.getName() + "'")
                .success(true)
                .timestamp(now)
                .build());

        return saved;
    }

    @Override
    public CaptaincyTransferRequest respondToTransfer(String userId, String transferId, boolean accept) {
        CaptaincyTransferRequest transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new TransferNotFoundException(transferId));

        if (transfer.getStatus() != TransferRequestStatus.PENDING) {
            throw new InvalidTransferStateException("Transfer request is no longer pending");
        }

        boolean isCaptainInitiated = "CAPTAIN".equals(transfer.getInitiatedBy());
        String expectedRespondent = isCaptainInitiated ? transfer.getNewCaptainId() : transfer.getCurrentCaptainId();

        if (!expectedRespondent.equals(userId)) {
            throw new UnauthorizedTeamActionException("Only the designated respondent can respond to this transfer");
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        transfer.setRespondedAt(now);

        if (accept) {
            transfer.setStatus(TransferRequestStatus.ACCEPTED);
            executeCaptaincyTransfer(transfer, now);

            auditRepository.save(AuditEvent.builder()
                    .teamId(transfer.getTeamId())
                    .userId(userId)
                    .actionType(AuditActionType.CAPTAINCY_TRANSFER_ACCEPTED)
                    .description("Captaincy transferred from " + transfer.getCurrentCaptainId()
                            + " to " + transfer.getNewCaptainId())
                    .success(true)
                    .timestamp(now)
                    .build());
        } else {
            transfer.setStatus(TransferRequestStatus.REJECTED);

            auditRepository.save(AuditEvent.builder()
                    .teamId(transfer.getTeamId())
                    .userId(userId)
                    .actionType(AuditActionType.CAPTAINCY_TRANSFER_REJECTED)
                    .description("Captaincy transfer rejected by " + userId)
                    .success(true)
                    .timestamp(now)
                    .build());
        }

        String recipientId = isCaptainInitiated ? transfer.getCurrentCaptainId() : transfer.getNewCaptainId();
        notificationPort.publishTeamLinkResponse(transfer.getTeamId(), transfer.getTeamName(),
                transferId, recipientId, accept);

        return transferRepository.save(transfer);
    }

    private void executeCaptaincyTransfer(CaptaincyTransferRequest transfer, LocalDateTime now) {
        Team team = teamRepository.findById(transfer.getTeamId())
                .orElseThrow(() -> new TeamNotFoundException(transfer.getTeamId()));

        for (TeamMember member : team.getMembers()) {
            if (member.getUserId().equals(transfer.getCurrentCaptainId())) {
                member.setRole(TeamMemberRole.PLAYER);
            } else if (member.getUserId().equals(transfer.getNewCaptainId())) {
                member.setRole(TeamMemberRole.CAPTAIN);
            }
        }

        team.setCaptainId(transfer.getNewCaptainId());
        team.setUpdatedAt(now);
        teamRepository.save(team);
    }

    private void validateNoPendingTransfer(String teamId) {
        transferRepository.findByTeamIdAndStatus(teamId, TransferRequestStatus.PENDING)
                .ifPresent(existing -> {
                    throw new InvalidTransferStateException("A pending captaincy transfer already exists for this team");
                });
    }
}
