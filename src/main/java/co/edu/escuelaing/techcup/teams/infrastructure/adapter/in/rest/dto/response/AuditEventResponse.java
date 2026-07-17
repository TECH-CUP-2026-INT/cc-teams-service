package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Evento de auditoría del servicio de equipos")
public class AuditEventResponse {

    @Schema(description = "ID del evento", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID del equipo relacionado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID teamId;

    @Schema(description = "ID del usuario que realizó la acción", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID userId;

    @Schema(description = "Tipo de acción", example = "TEAM_CREATED")
    private AuditActionType actionType;

    @Schema(description = "Descripción del evento")
    private String description;

    @Schema(description = "Indica si la acción fue exitosa", example = "true")
    private boolean success;

    @Schema(description = "Fecha y hora del evento")
    private LocalDateTime timestamp;
}
