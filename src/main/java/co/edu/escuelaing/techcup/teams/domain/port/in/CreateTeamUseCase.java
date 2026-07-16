package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.model.Team;

import java.util.UUID;

public interface CreateTeamUseCase {

    Team createTeam(UUID captainId, String captainName, String teamName, byte[] logo, String logoContentType, String colors);
}
