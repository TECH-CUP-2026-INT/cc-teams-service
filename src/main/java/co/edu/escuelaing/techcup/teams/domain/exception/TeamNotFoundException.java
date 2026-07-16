package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class TeamNotFoundException extends DomainException {

    public TeamNotFoundException(UUID teamId) {
        super("TEAM_NOT_FOUND", "Team not found: " + teamId);
    }
}
