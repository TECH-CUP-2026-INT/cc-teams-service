package co.edu.escuelaing.techcup.teams.controller;

import co.edu.escuelaing.techcup.teams.dto.*;
import co.edu.escuelaing.techcup.teams.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for team management operations.
 * The authenticated user's email is extracted from the JWT principal.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Creates a new team. The authenticated user becomes the captain.
     * POST /api/teams
     */
    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(
            @Valid @RequestBody CreateTeamRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        TeamResponse response = teamService.createTeam(request, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Returns the lineup of a team.
     * GET /api/teams/{id}/lineup
     */
    @GetMapping("/{id}/lineup")
    public ResponseEntity<LineupResponse> getLineup(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(teamService.getLineup(id, principal.getUsername()));
    }

    /**
     * Deletes a team.
     * DELETE /api/teams/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteTeam(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {

        teamService.deleteTeam(id, principal.getUsername());
        return ResponseEntity.ok(new ApiResponse("Team deleted successfully.", true));
    }
}
