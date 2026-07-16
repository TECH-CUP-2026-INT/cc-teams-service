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
@Schema(description = "Datos para iniciar una transferencia de capitanía")
public class InitiateTransferRequest {

    @NotNull(message = "New captain ID is required")
    @Schema(description = "ID del jugador que recibirá la capitanía", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID newCaptainId;
}
