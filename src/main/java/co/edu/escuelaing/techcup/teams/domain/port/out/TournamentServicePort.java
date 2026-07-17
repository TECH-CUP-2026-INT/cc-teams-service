package co.edu.escuelaing.techcup.teams.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TournamentServicePort {

    List<TournamentTeam> getRegisteredTeams(UUID tournamentId);

    Enrollment enrollTeam(UUID tournamentId, UUID teamId);

    /**
     * Checks whether a team has an active/in-progress tournament enrollment.
     * Tournament Service previously exposed {@code GET /tournaments/by-team/{teamId}/active}
     * for this; as of this writing that endpoint is gone from their repo. Until they bring
     * it back (or confirm a replacement contract), the adapter fails open (returns false)
     * on any Feign error so this never blocks callers — it just won't report true positives yet.
     */
    boolean hasActiveEnrollment(UUID teamId);

    record TournamentTeam(UUID teamId, String teamName, String registrationStatus, String logoUrl) {}

    record Enrollment(UUID enrollmentId, String status, LocalDateTime reservationExpiresAt) {}
}
