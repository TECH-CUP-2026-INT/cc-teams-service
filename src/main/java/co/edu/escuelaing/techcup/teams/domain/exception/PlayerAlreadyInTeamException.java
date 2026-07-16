package co.edu.escuelaing.techcup.teams.domain.exception;

import java.util.UUID;

public class PlayerAlreadyInTeamException extends DomainException {

    public PlayerAlreadyInTeamException(UUID userId) {
        super("PLAYER_ALREADY_IN_TEAM", "Player is already a member of a team: " + userId);
    }
}
