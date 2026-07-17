package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mensaje de respuesta genérico")
public class MessageResponse {

    @Schema(description = "Mensaje informativo", example = "Operación exitosa")
    private String message;
}
