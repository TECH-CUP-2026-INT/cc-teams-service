package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.model.TeamMember;
import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamRosterUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTeamRosterUseCaseImpl implements GetTeamRosterUseCase {

    private final TeamRepositoryPort teamRepository;

    @Override
    public Optional<TeamRoster> getTeamRosterByPlayer(UUID playerId) {
        return teamRepository.findByMembersUserId(playerId)
                .map(team -> new TeamRoster(
                        team.getId(),
                        team.getMembers().stream().map(TeamMember::getUserId).toList()));
    }
}
