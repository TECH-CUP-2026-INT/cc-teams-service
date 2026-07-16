package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamInfoUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Endpoint sin prefijo de version ni autenticacion, consumido por
 * mk-tournament-service (TeamServiceFeignClient: GET /teams/{teamId}).
 * El contrato (path y forma de la respuesta) lo definio ese repo; se replica
 * aqui tal cual para desbloquear la inscripcion de equipos a torneos.
 */
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Integracion Tournament", description = "Endpoint interno consumido por mk-tournament-service")
public class TeamInfoController {

    private final GetTeamInfoUseCase getTeamInfoUseCase;

    @GetMapping("/{teamId}")
    @Operation(summary = "Info de equipo para Tournament Service", description = "Devuelve nombre y tamano de roster de un equipo. Sin autenticacion: llamado servicio-a-servicio por mk-tournament-service.")
    public ResponseEntity<TeamInfoResponse> getTeamInfo(@PathVariable UUID teamId) {
        GetTeamInfoUseCase.TeamInfo info = getTeamInfoUseCase.getTeamInfo(teamId);
        return ResponseEntity.ok(new TeamInfoResponse(info.teamName(), info.rosterSize()));
    }
}
