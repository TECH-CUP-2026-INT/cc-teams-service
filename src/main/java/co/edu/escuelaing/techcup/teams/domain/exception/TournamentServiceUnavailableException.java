package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class TournamentServiceUnavailableException extends DomainException {

    public TournamentServiceUnavailableException(UUID tournamentId, Throwable cause) {
        super("TOURNAMENT_SERVICE_UNAVAILABLE", "Could not reach Tournament Service for tournament " + tournamentId);
        initCause(cause);
    }
}
