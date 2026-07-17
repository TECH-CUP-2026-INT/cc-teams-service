package co.edu.escuelaing.techcup.teams.domain.port.in;

import java.util.UUID;

public interface GetTeamInfoUseCase {

    TeamInfo getTeamInfo(UUID teamId);

    record TeamInfo(String teamName, int rosterSize) {}
}
