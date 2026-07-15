package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Datos para la creación de un equipo")
public class CreateTeamRequest {

    @NotBlank(message = "Team name is required")
    @Size(min = 4, max = 100, message = "Team name must be between 4 and 100 characters")
    @Schema(description = "Nombre único del equipo en la plataforma", example = "Los Halcones FC")
    private String name;

    @Schema(description = "Colores del equipo (formato libre o código hex)", example = "#FF0000,#FFFFFF")
    private String colors;
}
