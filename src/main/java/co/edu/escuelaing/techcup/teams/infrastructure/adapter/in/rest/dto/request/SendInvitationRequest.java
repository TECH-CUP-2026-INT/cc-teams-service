package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para enviar una invitación a un jugador")
public class SendInvitationRequest {

    @NotNull(message = "Invited user ID is required")
    @Schema(description = "ID del jugador a invitar al equipo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID invitedUserId;
}
