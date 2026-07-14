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
        TeamRosterResponse response = teamService.getTeamRoster(teamId);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Update team", description = "Updates team information")
    @PatchMapping("/{teamId}")
    public ResponseEntity<TeamUpdateResponse> updateTeam(
            @PathVariable String teamId,
            @RequestHeader("X-User-Id") String captainId,
            @Valid @RequestBody TeamUpdateRequest request) {
        TeamUpdateResponse response = teamService.updateTeam(teamId, captainId, request);
        return ResponseEntity.ok(response);
    }
}