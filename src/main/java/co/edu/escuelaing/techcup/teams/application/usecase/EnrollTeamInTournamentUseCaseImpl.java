package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotEligibleForTournamentException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.UnauthorizedTeamActionException;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.port.in.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollTeamInTournamentUseCaseImpl implements EnrollTeamInTournamentUseCase {

    private final TeamRepositoryPort teamRepository;
    private final TournamentServicePort tournamentServicePort;
    private final AuditEventRepositoryPort auditRepository;

    @Override
    public TournamentServicePort.Enrollment enrollTeam(UUID captainId, UUID teamId, UUID tournamentId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        if (!team.getCaptainId().equals(captainId)) {
            throw new UnauthorizedTeamActionException("Only the team captain can enroll the team in a tournament");
        }

        int rosterSize = team.getMembers().size();
        if (rosterSize < Team.MIN_MEMBERS_FOR_TOURNAMENT) {
            throw new TeamNotEligibleForTournamentException(teamId, rosterSize, Team.MIN_MEMBERS_FOR_TOURNAMENT);
        }

        TournamentServicePort.Enrollment enrollment = tournamentServicePort.enrollTeam(tournamentId, teamId);

        auditRepository.save(AuditEvent.builder()
                .teamId(teamId)
                .userId(captainId)
                .actionType(AuditActionType.TEAM_ENROLLED_IN_TOURNAMENT)
                .description("Team '" + team.getName() + "' enrolled in tournament " + tournamentId
                        + " (enrollment " + enrollment.enrollmentId() + ", status " + enrollment.status() + ")")
                .success(true)
                .timestamp(LocalDateTime.now(ZoneOffset.UTC))
                .build());

        return enrollment;
    }
}
