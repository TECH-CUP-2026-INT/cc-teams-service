package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.port.in.CheckPlayerActiveTournamentUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamInfoUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamRosterUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.PlayerActiveTournamentResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamInfoResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamRosterResponse;
import io.swagger.v3.oas.annotations.Hidden;
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
 * Endpoints sin prefijo de version ni autenticacion, consumidos servicio-a-servicio.
 * GET /{teamId} lo consume mk-tournament-service (contrato definido por ese repo).
 * GET /by-player/{playerId}/roster lo consume users-players-service para validar
 * unicidad de numero de camiseta dentro del equipo (perfil deportivo, TC-17).
 * GET /by-player/{playerId}/active-tournament lo consume users-players-service para
 * TC-16/TC-17/TC-19 (bloquear edicion/deshabilitacion si el jugador tiene torneo
 * activo). Internamente depende de un endpoint de Tournament Service que hoy no
 * existe (ver TournamentServicePort) — falla abierto mientras tanto.
 */
@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@Tag(name = "Integracion inter-servicios", description = "Endpoints internos consumidos por mk-tournament-service y users-players-service")
public class TeamInfoController {

    private final GetTeamInfoUseCase getTeamInfoUseCase;
    private final GetTeamRosterUseCase getTeamRosterUseCase;
    private final CheckPlayerActiveTournamentUseCase checkPlayerActiveTournamentUseCase;

    @GetMapping("/{teamId}")
    @Hidden
    @Operation(summary = "Info de equipo para Tournament Service", description = "Devuelve nombre y tamano de roster de un equipo. Sin autenticacion: llamado servicio-a-servicio por mk-tournament-service.")
    public ResponseEntity<TeamInfoResponse> getTeamInfo(@PathVariable UUID teamId) {
        GetTeamInfoUseCase.TeamInfo info = getTeamInfoUseCase.getTeamInfo(teamId);
        return ResponseEntity.ok(new TeamInfoResponse(info.teamName(), info.rosterSize()));
    }

    @GetMapping("/by-player/{playerId}/roster")
    @Hidden
    @Operation(summary = "Roster del equipo de un jugador para Users & Players Service",
            description = "Devuelve el id del equipo y los ids de todos sus miembros, dado el id de un jugador. "
                    + "Si el jugador no pertenece a ningun equipo, retorna teamId nulo y memberIds vacio. "
                    + "Sin autenticacion: llamado servicio-a-servicio por users-players-service para validar "
                    + "unicidad de numero de camiseta.")
    public ResponseEntity<TeamRosterResponse> getTeamRosterByPlayer(@PathVariable UUID playerId) {
        return getTeamRosterUseCase.getTeamRosterByPlayer(playerId)
                .map(roster -> ResponseEntity.ok(new TeamRosterResponse(roster.teamId(), roster.memberIds())))
                .orElseGet(() -> ResponseEntity.ok(TeamRosterResponse.empty()));
    }

    @GetMapping("/by-player/{playerId}/active-tournament")
    @Hidden
    @Operation(summary = "Estado de torneo activo del equipo de un jugador para Users & Players Service",
            description = "Resuelve el equipo del jugador y consulta a Tournament Service si tiene una "
                    + "inscripcion activa/en curso. Retorna false si el jugador no tiene equipo, o si "
                    + "Tournament Service no esta disponible (falla abierto). Sin autenticacion: llamado "
                    + "servicio-a-servicio por users-players-service.")
    public ResponseEntity<PlayerActiveTournamentResponse> hasActiveTournament(@PathVariable UUID playerId) {
        boolean activo = checkPlayerActiveTournamentUseCase.hasActiveTournament(playerId);
        return ResponseEntity.ok(new PlayerActiveTournamentResponse(playerId, activo));
    }
}
