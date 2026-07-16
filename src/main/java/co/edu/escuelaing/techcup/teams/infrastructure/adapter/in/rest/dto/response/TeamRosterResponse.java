package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import java.util.List;
import java.util.UUID;

public record TeamRosterResponse(UUID teamId, List<UUID> memberIds) {

    public static TeamRosterResponse empty() {
        return new TeamRosterResponse(null, List.of());
    }
}
