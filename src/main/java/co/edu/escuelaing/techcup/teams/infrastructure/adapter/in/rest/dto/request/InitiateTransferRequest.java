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
@Schema(description = "Datos para iniciar una transferencia de capitanía")
public class InitiateTransferRequest {

    @NotBlank(message = "New captain ID is required")
    @Schema(description = "ID del jugador que recibirá la capitanía", example = "player-456")
    private String newCaptainId;
}
