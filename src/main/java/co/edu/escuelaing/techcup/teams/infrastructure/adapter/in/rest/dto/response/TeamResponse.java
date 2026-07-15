package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

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
@Schema(description = "Datos de un equipo")
public class TeamResponse {

    @Schema(description = "ID único del equipo", example = "team-abc123")
    private String id;

    @Schema(description = "Nombre del equipo", example = "Los Halcones FC")
    private String name;

    @Schema(description = "Colores del equipo", example = "#FF0000,#FFFFFF")
    private String colors;

    @Schema(description = "ID del capitán", example = "user-123")
    private String captainId;

    @Schema(description = "Cantidad de miembros del equipo", example = "5")
    private int memberCount;

    @Schema(description = "Fecha de creación del equipo")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
