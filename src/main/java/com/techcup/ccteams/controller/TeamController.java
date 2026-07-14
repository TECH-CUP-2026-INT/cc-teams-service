package com.techcup.ccteams.controller;

import com.techcup.ccteams.dto.request.TeamUpdateRequest;
import com.techcup.ccteams.dto.response.TeamRosterResponse;
import com.techcup.ccteams.dto.response.TeamUpdateResponse;
import com.techcup.ccteams.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {
    
    private final TeamService teamService;
    
    // SCRUM-30: Consultar plantilla del equipo
    @GetMapping("/{teamId}/roster")
    public ResponseEntity<TeamRosterResponse> getTeamRoster(@PathVariable UUID teamId) {
        TeamRosterResponse response = teamService.getTeamRoster(teamId);
        return ResponseEntity.ok(response);
    }
    
    // SCRUM-27: Actualizar equipo
    @PatchMapping("/{teamId}")
    public ResponseEntity<TeamUpdateResponse> updateTeam(
            @PathVariable UUID teamId,
            @RequestHeader("X-User-Id") UUID captainId,
            @Valid @RequestBody TeamUpdateRequest request) {
        
        TeamUpdateResponse response = teamService.updateTeam(teamId, captainId, request);
        return ResponseEntity.ok(response);
    }
}