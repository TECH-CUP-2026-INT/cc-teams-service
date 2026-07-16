package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
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
@Schema(description = "Datos de una invitación de equipo")
public class TeamInvitationResponse {

    @Schema(description = "ID de la invitación", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "ID del equipo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID teamId;

    @Schema(description = "Nombre del equipo", example = "Los Halcones FC")
    private String teamName;

    @Schema(description = "ID del jugador invitado", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID invitedUserId;

    @Schema(description = "ID del capitán que envió la invitación", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID invitedBy;

    @Schema(description = "Estado de la invitación", example = "PENDING")
    private InvitationStatus status;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de respuesta")
    private LocalDateTime respondedAt;
}
