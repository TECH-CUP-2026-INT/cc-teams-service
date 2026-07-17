package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.identity.dto;

public record TokenValidationResponseDTO(
        boolean valid,
        String userId,
        String email,
        String role
) {
}
