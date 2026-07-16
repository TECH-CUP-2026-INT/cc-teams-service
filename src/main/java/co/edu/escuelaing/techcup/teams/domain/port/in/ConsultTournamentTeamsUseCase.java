package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;

import java.util.List;
import java.util.UUID;

public interface ConsultTournamentTeamsUseCase {

    List<TournamentServicePort.TournamentTeam> getRegisteredTeams(UUID tournamentId);
}
