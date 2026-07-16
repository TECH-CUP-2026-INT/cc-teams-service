package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto;

import java.util.UUID;

/**
 * Contrato asumido de {@code GET /tournaments/by-team/{teamId}/active} en
 * Tournament Service, tal como se observó antes de que ese endpoint
 * desapareciera de su repo. Si lo reconstruyen con una forma distinta, solo
 * este DTO y {@link co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.TournamentFeignClient}
 * necesitan ajustarse.
 */
public record TeamActiveEnrollmentResponseDTO(UUID teamId, boolean hasActiveEnrollment) {
}
