package co.edu.escuelaing.techcup.teams.domain.exception;

public class TeamNameAlreadyExistsException extends DomainException {

    public TeamNameAlreadyExistsException(String name) {
        super("TEAM_NAME_EXISTS", "A team with name '" + name + "' already exists");
    }
}
