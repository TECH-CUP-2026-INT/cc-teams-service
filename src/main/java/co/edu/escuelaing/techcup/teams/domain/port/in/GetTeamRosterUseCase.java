package co.edu.escuelaing.techcup.teams.domain.port.in;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Resolves the team a player belongs to and its full member roster.
 * Consumed by users-players-service to validate jersey number uniqueness
 * within a team (sports profile update).
 */
public interface GetTeamRosterUseCase {

    Optional<TeamRoster> getTeamRosterByPlayer(UUID playerId);

    record TeamRoster(UUID teamId, List<UUID> memberIds) {}
}
