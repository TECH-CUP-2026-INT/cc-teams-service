package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.port.in.CreateTeamUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request.CreateTeamRequest;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamMapper;
import co.edu.escuelaing.techcup.teams.shared.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Tag(name = "Equipos", description = "Operaciones de gestión de equipos")
public class TeamController {

    private final CreateTeamUseCase createTeamUseCase;
    private final TeamMapper teamMapper;
    private final JwtUtil jwtUtil;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Crear equipo", description = "Permite al Capitán crear un nuevo equipo con nombre, logo y colores. El nombre debe ser único en la plataforma.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Equipo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "409", description = "Nombre de equipo ya existe")
    })
    public ResponseEntity<TeamResponse> createTeam(
            @RequestPart("team") @Valid CreateTeamRequest request,
            @RequestPart("logo") MultipartFile logo,
            Authentication authentication) throws IOException {

        String token = (String) authentication.getCredentials();
        String captainId = (String) authentication.getPrincipal();
        String captainName = jwtUtil.extractFullName(token);

        Team created = createTeamUseCase.createTeam(
                captainId,
                captainName != null ? captainName : "Captain",
                request.getName(),
                logo.getBytes(),
                logo.getContentType(),
                request.getColors()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(teamMapper.toResponse(created));
    }
}
