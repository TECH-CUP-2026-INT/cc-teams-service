package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta del jugador a una invitación de equipo")
public class InvitationResponseRequest {

    @NotNull(message = "Accept field is required")
    @Schema(description = "true para aceptar la invitación, false para rechazarla", example = "true")
    private Boolean accept;
}
