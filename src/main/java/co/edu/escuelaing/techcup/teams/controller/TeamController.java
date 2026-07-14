package co.edu.escuelaing.techcup.teams.controller;

import co.edu.escuelaing.techcup.teams.dto.ApiResponse;
import co.edu.escuelaing.techcup.teams.dto.TransferCaptainRequest;
import co.edu.escuelaing.techcup.teams.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for team operations.
 * All endpoints require a valid JWT from cc-identity-service.
 */
@RestController
@RequestMapping("/api/teams")
@Tag(name = "Teams", description = "Team management endpoints")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Transfers captaincy to another team member (SCRUM-22).
     * The authenticated user must be the current captain.
     *
     * PATCH /api/teams/{teamId}/captain
     */
    @PatchMapping("/{teamId}/captain")
    @Operation(summary = "Transfer captaincy",
               description = "The current captain delegates captaincy to another active team member")
    public ResponseEntity<ApiResponse> transferCaptaincy(
            @PathVariable Long teamId,
            @Valid @RequestBody TransferCaptainRequest request,
            Authentication authentication) {

        String currentCaptainEmail = authentication.getName();
        ApiResponse response = teamService.transferCaptaincy(teamId, currentCaptainEmail, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Disables a team member (SCRUM-61).
     * Only the captain can perform this action.
     *
     * PATCH /api/teams/{teamId}/members/{memberEmail}/disable
     */
    @PatchMapping("/{teamId}/members/{memberEmail}/disable")
    @Operation(summary = "Disable team member",
               description = "The captain disables an active member, preventing further participation")
    public ResponseEntity<ApiResponse> disableMember(
            @PathVariable Long teamId,
            @PathVariable String memberEmail,
            Authentication authentication) {

        String captainEmail = authentication.getName();
        ApiResponse response = teamService.disableMember(teamId, captainEmail, memberEmail);
        return ResponseEntity.ok(response);
    }
}
