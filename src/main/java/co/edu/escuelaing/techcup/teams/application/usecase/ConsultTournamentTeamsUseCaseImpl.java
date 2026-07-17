package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.port.in.ConsultTournamentTeamsUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultTournamentTeamsUseCaseImpl implements ConsultTournamentTeamsUseCase {

    private final TournamentServicePort tournamentServicePort;

    @Override
    public List<TournamentServicePort.TournamentTeam> getRegisteredTeams(UUID tournamentId) {
        return tournamentServicePort.getRegisteredTeams(tournamentId);
    }
}
