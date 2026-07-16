package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class TeamFullException extends DomainException {

    public TeamFullException(UUID teamId) {
        super("TEAM_FULL", "Team has reached maximum capacity (12 members): " + teamId);
    }
}
