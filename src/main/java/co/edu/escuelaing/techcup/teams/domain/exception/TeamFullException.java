package co.edu.escuelaing.techcup.teams.domain.exception;

public class TeamFullException extends DomainException {

    public TeamFullException(String teamId) {
        super("TEAM_FULL", "Team has reached maximum capacity (12 members): " + teamId);
    }
}
