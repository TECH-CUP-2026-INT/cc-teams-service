package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response;

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
@Schema(description = "Datos de un equipo")
public class TeamResponse {

    @Schema(description = "ID único del equipo", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Nombre del equipo", example = "Los Halcones FC")
    private String name;

    @Schema(description = "Colores del equipo", example = "#FF0000,#FFFFFF")
    private String colors;

    @Schema(description = "ID del capitán", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID captainId;

    @Schema(description = "Cantidad de miembros del equipo", example = "5")
    private int memberCount;

    @Schema(description = "Fecha de creación del equipo")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime updatedAt;
}
