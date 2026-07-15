package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
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
@Schema(description = "Datos de una solicitud de transferencia de capitanía")
public class CaptaincyTransferResponse {

    @Schema(description = "ID de la solicitud", example = "transfer-abc123")
    private String id;

    @Schema(description = "ID del equipo", example = "team-abc123")
    private String teamId;

    @Schema(description = "Nombre del equipo", example = "Los Halcones FC")
    private String teamName;

    @Schema(description = "ID del capitán actual", example = "captain-123")
    private String currentCaptainId;

    @Schema(description = "ID del nuevo capitán propuesto", example = "player-456")
    private String newCaptainId;

    @Schema(description = "Quién inició la solicitud: CAPTAIN o PLAYER", example = "CAPTAIN")
    private String initiatedBy;

    @Schema(description = "Estado de la solicitud", example = "PENDING")
    private TransferRequestStatus status;

    @Schema(description = "Fecha de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de respuesta")
    private LocalDateTime respondedAt;
}
