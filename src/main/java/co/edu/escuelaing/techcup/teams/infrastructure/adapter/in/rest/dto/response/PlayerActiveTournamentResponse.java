package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import java.util.UUID;

public record PlayerActiveTournamentResponse(UUID playerId, boolean hasActiveTournament) {
}
