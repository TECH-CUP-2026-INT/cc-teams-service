package co.edu.escuelaing.techcup.teams.domain.exception;

public class TeamNotFoundException extends DomainException {

    public TeamNotFoundException(String teamId) {
        super("TEAM_NOT_FOUND", "Team not found: " + teamId);
    }
}
