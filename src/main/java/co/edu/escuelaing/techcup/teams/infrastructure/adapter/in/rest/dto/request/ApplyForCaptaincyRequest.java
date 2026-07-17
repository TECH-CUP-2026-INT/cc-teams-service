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
@Schema(description = "Datos para solicitar la capitanía de un equipo")
public class ApplyForCaptaincyRequest {

    @NotBlank(message = "Player name is required")
    @Schema(description = "Nombre completo del jugador que solicita la capitanía", example = "Grace Hopper")
    private String playerName;
}
