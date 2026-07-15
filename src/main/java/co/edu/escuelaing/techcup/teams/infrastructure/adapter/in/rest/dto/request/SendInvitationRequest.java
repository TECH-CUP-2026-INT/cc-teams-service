package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para enviar una invitación a un jugador")
public class SendInvitationRequest {

    @NotBlank(message = "Invited user ID is required")
    @Schema(description = "ID del jugador a invitar al equipo", example = "player-123")
    private String invitedUserId;
}
