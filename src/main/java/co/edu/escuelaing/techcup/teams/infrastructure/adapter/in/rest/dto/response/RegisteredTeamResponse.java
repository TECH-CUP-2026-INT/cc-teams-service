package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import java.util.UUID;

public record RegisteredTeamResponse(UUID teamId, String teamName, String registrationStatus, String logoUrl) {}
