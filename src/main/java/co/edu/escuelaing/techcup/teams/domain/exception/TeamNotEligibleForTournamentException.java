package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class TeamNotEligibleForTournamentException extends DomainException {

    public TeamNotEligibleForTournamentException(UUID teamId, int currentSize, int required) {
        super("TEAM_NOT_ELIGIBLE_FOR_TOURNAMENT",
                "Team " + teamId + " has " + currentSize + " members; at least " + required + " are required to enroll in a tournament");
    }
}
