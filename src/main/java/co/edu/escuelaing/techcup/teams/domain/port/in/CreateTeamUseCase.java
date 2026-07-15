package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.model.Team;

public interface CreateTeamUseCase {

    Team createTeam(String captainId, String captainName, String teamName, byte[] logo, String logoContentType, String colors);
}
