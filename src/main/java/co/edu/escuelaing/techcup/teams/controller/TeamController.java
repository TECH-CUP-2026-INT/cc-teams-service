package co.edu.escuelaing.techcup.teams.controller;

import co.edu.escuelaing.techcup.teams.dto.ApiResponse;
import co.edu.escuelaing.techcup.teams.dto.TransferCaptainRequest;
import co.edu.escuelaing.techcup.teams.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
    @Operation(
        summary = "Transfer captaincy (SCRUM-22)",
        description = "The authenticated captain delegates captaincy to another active team member. The caller must be the current captain.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Captaincy transferred successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400", description = "Invalid request body (missing or malformed email)",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", description = "Business rule violation (not captain, member not found, etc.)",
            content = @Content)
    })
    public ResponseEntity<ApiResponse> transferCaptaincy(
            @Parameter(description = "MongoDB ID of the team", example = "665f1a2b3c4d5e6f7a8b9c0d")
            @PathVariable String teamId,
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
    @Operation(
        summary = "Disable team member (SCRUM-61)",
        description = "The authenticated captain disables an active member, preventing further participation. The captain cannot disable themselves.",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200", description = "Member disabled successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401", description = "Missing or invalid JWT token",
            content = @Content),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "500", description = "Business rule violation (not captain, self-disable, member not found, already inactive)",
            content = @Content)
    })
    public ResponseEntity<ApiResponse> disableMember(
            @Parameter(description = "MongoDB ID of the team", example = "665f1a2b3c4d5e6f7a8b9c0d")
            @PathVariable String teamId,
            @Parameter(description = "Email of the member to disable", example = "player@techcup.co")
            @PathVariable String memberEmail,
            Authentication authentication) {

        String captainEmail = authentication.getName();
        ApiResponse response = teamService.disableMember(teamId, captainEmail, memberEmail);
        return ResponseEntity.ok(response);
    }
}
