package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.enums.TeamMemberRole;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNameAlreadyExistsException;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.model.TeamMember;
import co.edu.escuelaing.techcup.teams.domain.port.in.CreateTeamUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateTeamUseCaseImpl implements CreateTeamUseCase {

    private final TeamRepositoryPort teamRepository;
    private final AuditEventRepositoryPort auditRepository;

    @Override
    public Team createTeam(UUID captainId, String captainName, String teamName,
                           byte[] logo, String logoContentType, String colors) {
        if (teamRepository.existsByName(teamName)) {
            throw new TeamNameAlreadyExistsException(teamName);
        }

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        TeamMember captain = TeamMember.builder()
                .userId(captainId)
                .fullName(captainName)
                .role(TeamMemberRole.CAPTAIN)
                .joinedAt(now)
                .build();

        Team team = Team.builder()
                .name(teamName)
                .logo(logo)
                .logoContentType(logoContentType)
                .colors(colors)
                .captainId(captainId)
                .members(new ArrayList<>(List.of(captain)))
                .createdAt(now)
                .updatedAt(now)
                .build();

        Team saved = teamRepository.save(team);

        auditRepository.save(AuditEvent.builder()
                .teamId(saved.getId())
                .userId(captainId)
                .actionType(AuditActionType.TEAM_CREATED)
                .description("Team '" + teamName + "' created by captain " + captainId)
                .success(true)
                .timestamp(now)
                .build());

        return saved;
    }
}
