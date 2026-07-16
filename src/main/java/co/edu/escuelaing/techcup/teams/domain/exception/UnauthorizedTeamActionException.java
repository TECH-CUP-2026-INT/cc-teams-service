package co.edu.escuelaing.techcup.teams.domain.exception;

public class UnauthorizedTeamActionException extends DomainException {

    public UnauthorizedTeamActionException(String message) {
        super("UNAUTHORIZED_TEAM_ACTION", message);
    }
}
