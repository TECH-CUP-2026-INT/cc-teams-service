package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto;

import java.util.UUID;

public record RegisteredTeamResponseDTO(UUID teamId, String teamName, String registrationStatus, String logoUrl) {}
