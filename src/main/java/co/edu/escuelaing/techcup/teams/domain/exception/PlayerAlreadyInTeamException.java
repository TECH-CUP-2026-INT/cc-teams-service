package co.edu.escuelaing.techcup.teams.domain.exception;

public class PlayerAlreadyInTeamException extends DomainException {

    public PlayerAlreadyInTeamException(String userId) {
        super("PLAYER_ALREADY_IN_TEAM", "Player is already a member of a team: " + userId);
    }
}
