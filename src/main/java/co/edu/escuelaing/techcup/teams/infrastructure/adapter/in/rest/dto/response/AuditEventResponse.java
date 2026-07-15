package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Evento de auditoría del servicio de equipos")
public class AuditEventResponse {

    @Schema(description = "ID del evento", example = "audit-abc123")
    private String id;

    @Schema(description = "ID del equipo relacionado", example = "team-abc123")
    private String teamId;

    @Schema(description = "ID del usuario que realizó la acción", example = "user-123")
    private String userId;

    @Schema(description = "Tipo de acción", example = "TEAM_CREATED")
    private AuditActionType actionType;

    @Schema(description = "Descripción del evento")
    private String description;

    @Schema(description = "Indica si la acción fue exitosa", example = "true")
    private boolean success;

    @Schema(description = "Fecha y hora del evento")
    private LocalDateTime timestamp;
}
