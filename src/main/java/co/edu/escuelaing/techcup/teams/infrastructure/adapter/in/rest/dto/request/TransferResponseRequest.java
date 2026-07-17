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
@Schema(description = "Respuesta a una solicitud de transferencia de capitanía")
public class TransferResponseRequest {

    @NotNull(message = "Accept field is required")
    @Schema(description = "true para aceptar la transferencia, false para rechazarla", example = "true")
    private Boolean accept;
}
