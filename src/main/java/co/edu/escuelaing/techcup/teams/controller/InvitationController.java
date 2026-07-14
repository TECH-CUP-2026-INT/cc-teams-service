package co.edu.escuelaing.techcup.teams.controller;

import co.edu.escuelaing.techcup.teams.dto.InvitationResponse;
import co.edu.escuelaing.techcup.teams.dto.InvitePlayerRequest;
import co.edu.escuelaing.techcup.teams.dto.RespondInvitationRequest;
import co.edu.escuelaing.techcup.teams.service.InvitationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for team invitation management.
 */
@RestController
@RequestMapping("/api")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    /**
     * Sends an invitation to a player. Only the team captain can do this.
     * POST /api/teams/{teamId}/invitations
     */
    @PostMapping("/teams/{teamId}/invitations")
    public ResponseEntity<InvitationResponse> sendInvitation(
            @PathVariable UUID teamId,
            @Valid @RequestBody InvitePlayerRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        InvitationResponse response = invitationService.sendInvitation(teamId, request, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Allows the invited player to accept or reject an invitation.
     * PUT /api/invitations/{invitationId}/respond
     */
    @PutMapping("/invitations/{invitationId}/respond")
    public ResponseEntity<InvitationResponse> respondToInvitation(
            @PathVariable UUID invitationId,
            @Valid @RequestBody RespondInvitationRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        return ResponseEntity.ok(invitationService.respondToInvitation(invitationId, request, principal.getUsername()));
    }
}
