package co.edu.escuelaing.techcup.teams.domain.exception;

public class TournamentEnrollmentRejectedException extends DomainException {

    public TournamentEnrollmentRejectedException(String reason) {
        super("TOURNAMENT_ENROLLMENT_REJECTED", reason);
    }
}
