package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class TournamentNotFoundException extends DomainException {

    public TournamentNotFoundException(UUID tournamentId) {
        super("TOURNAMENT_NOT_FOUND", "Tournament not found: " + tournamentId);
    }
}
