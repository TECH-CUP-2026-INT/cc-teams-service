package com.techcup.ccteams.controller;

import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@Tag(name = "Teams", description = "Endpoints for managing teams")
public class TeamController {
    private final TeamService teamService;

    @Operation(summary = "Get team roster", description = "Returns the full roster of a team")
    @GetMapping("/{teamId}/roster")
    public ResponseEntity<TeamRosterResponse> getTeamRoster(@PathVariable String teamId) {
        return ResponseEntity.ok(teamService.getTeamRoster(teamId));
    }

    @Operation(summary = "Update team", description = "Updates team information. Only the captain can update.")
    @PatchMapping("/{teamId}")
    public ResponseEntity<TeamUpdateResponse> updateTeam(
            @PathVariable String teamId,
            @RequestHeader("X-User-Id") String captainId,
            @Valid @RequestBody TeamUpdateRequest request) {
        return ResponseEntity.ok(teamService.updateTeam(teamId, captainId, request));
    }

    /**
     * TC-26 — Retirar jugador del equipo.
     *
     * DELETE /api/teams/{teamId}/players/{playerId}
     *
     * Headers requeridos:
     *   X-Captain-Id — UUID del capitán que autoriza el retiro
     *
     * Respuestas:
     *   204 — jugador retirado exitosamente
     *   400 — el jugador ya fue retirado o el capitán intenta retirarse a sí mismo
     *   403 — quien solicita no es el capitán del equipo
     *   404 — equipo no encontrado o jugador no pertenece al equipo
     */
    @Operation(
            summary = "Remove player from team",
            description = "TC-26: Removes an existing player from the team. Only the captain can perform this action."
    )
    @DeleteMapping("/{teamId}/players/{playerId}")
    public ResponseEntity<Void> removePlayer(
            @PathVariable String teamId,
            @PathVariable String playerId,
            @RequestHeader("X-Captain-Id") String captainId) {
        teamService.removePlayer(teamId, playerId, captainId);
        return ResponseEntity.noContent().build();
    }
}