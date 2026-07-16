package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.domain.port.in.ManageInvitationUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request.InvitationResponseRequest;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request.SendInvitationRequest;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TeamInvitationResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamInvitationMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invitations")
@RequiredArgsConstructor
@Tag(name = "Invitaciones", description = "Gestión de invitaciones a equipos")
public class InvitationController {

    private final ManageInvitationUseCase manageInvitationUseCase;
    private final TeamInvitationMapper invitationMapper;

    @PostMapping("/teams/{teamId}")
    @Operation(summary = "Enviar invitación", description = "El Capitán envía una invitación a un jugador para unirse al equipo.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Invitación enviada exitosamente"),
            @ApiResponse(responseCode = "403", description = "Solo el capitán puede enviar invitaciones"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Jugador ya es miembro o equipo lleno")
    })
    public ResponseEntity<TeamInvitationResponse> sendInvitation(
            @PathVariable UUID teamId,
            @RequestBody @Valid SendInvitationRequest request,
            Authentication authentication) {

        UUID captainId = (UUID) authentication.getPrincipal();

        TeamInvitation invitation = manageInvitationUseCase.sendInvitation(
                captainId, teamId, request.getInvitedUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(invitationMapper.toResponse(invitation));
    }

    @PutMapping("/{invitationId}/respond")
    @Operation(summary = "Responder invitación", description = "El jugador invitado acepta o rechaza la invitación al equipo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Invitación ya no está pendiente"),
            @ApiResponse(responseCode = "403", description = "Solo el jugador invitado puede responder"),
            @ApiResponse(responseCode = "404", description = "Invitación no encontrada")
    })
    public ResponseEntity<TeamInvitationResponse> respondToInvitation(
            @PathVariable UUID invitationId,
            @RequestBody @Valid InvitationResponseRequest request,
            Authentication authentication) {

        UUID userId = (UUID) authentication.getPrincipal();
        String userName = request.getUserName();

        TeamInvitation invitation = manageInvitationUseCase.respondToInvitation(
                userId, userName != null ? userName : "Player", invitationId, request.getAccept());

        return ResponseEntity.ok(invitationMapper.toResponse(invitation));
    }

    @GetMapping("/my")
    @Operation(summary = "Consultar mis invitaciones", description = "Obtiene todas las invitaciones recibidas por el usuario autenticado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de invitaciones")
    })
    public ResponseEntity<List<TeamInvitationResponse>> getMyInvitations(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();

        List<TeamInvitationResponse> responses = manageInvitationUseCase.getInvitationsForUser(userId).stream()
                .map(invitationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/teams/{teamId}")
    @Operation(summary = "Consultar invitaciones del equipo", description = "El Capitán consulta todas las invitaciones enviadas por su equipo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de invitaciones del equipo"),
            @ApiResponse(responseCode = "403", description = "Solo el capitán puede ver las invitaciones"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    public ResponseEntity<List<TeamInvitationResponse>> getTeamInvitations(
            @PathVariable UUID teamId,
            Authentication authentication) {

        UUID captainId = (UUID) authentication.getPrincipal();

        List<TeamInvitationResponse> responses = manageInvitationUseCase.getInvitationsForTeam(captainId, teamId).stream()
                .map(invitationMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
