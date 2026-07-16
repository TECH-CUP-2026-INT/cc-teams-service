package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;

import java.util.UUID;

public interface EnrollTeamInTournamentUseCase {

    TournamentServicePort.Enrollment enrollTeam(UUID captainId, UUID teamId, UUID tournamentId);
}
