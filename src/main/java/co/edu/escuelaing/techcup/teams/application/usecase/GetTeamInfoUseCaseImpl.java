package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamInfoUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTeamInfoUseCaseImpl implements GetTeamInfoUseCase {

    private final TeamRepositoryPort teamRepository;

    @Override
    public TeamInfo getTeamInfo(UUID teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException(teamId));

        return new TeamInfo(team.getName(), team.getMembers().size());
    }
}
