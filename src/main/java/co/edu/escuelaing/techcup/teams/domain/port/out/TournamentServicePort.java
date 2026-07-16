package co.edu.escuelaing.techcup.teams.domain.port.out;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TournamentServicePort {

    List<TournamentTeam> getRegisteredTeams(UUID tournamentId);

    Enrollment enrollTeam(UUID tournamentId, UUID teamId);

    record TournamentTeam(UUID teamId, String teamName, String registrationStatus, String logoUrl) {}

    record Enrollment(UUID enrollmentId, String status, LocalDateTime reservationExpiresAt) {}
}
