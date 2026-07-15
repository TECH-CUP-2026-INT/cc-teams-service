package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.domain.port.in.TransferCaptaincyUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request.InitiateTransferRequest;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.request.TransferResponseRequest;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.CaptaincyTransferResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.CaptaincyTransferMapper;
import co.edu.escuelaing.techcup.teams.shared.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/captaincy")
@RequiredArgsConstructor
@Tag(name = "Capitanía", description = "Gestión de transferencia de capitanía")
public class CaptaincyController {

    private final TransferCaptaincyUseCase transferCaptaincyUseCase;
    private final CaptaincyTransferMapper transferMapper;
    private final JwtUtil jwtUtil;

    @PostMapping("/teams/{teamId}/transfer")
    @Operation(summary = "Iniciar transferencia de capitanía", description = "El Capitán selecciona un miembro del equipo para transferirle la capitanía. Se envía una notificación al jugador seleccionado.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud de transferencia creada"),
            @ApiResponse(responseCode = "400", description = "Ya existe una solicitud pendiente"),
            @ApiResponse(responseCode = "403", description = "Solo el capitán puede iniciar la transferencia"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    public ResponseEntity<CaptaincyTransferResponse> initiateTransfer(
            @PathVariable String teamId,
            @RequestBody @Valid InitiateTransferRequest request,
            Authentication authentication) {

        String captainId = (String) authentication.getPrincipal();

        CaptaincyTransferRequest transfer = transferCaptaincyUseCase.initiateTransfer(
                captainId, teamId, request.getNewCaptainId());

        return ResponseEntity.status(HttpStatus.CREATED).body(transferMapper.toResponse(transfer));
    }

    @PostMapping("/teams/{teamId}/apply")
    @Operation(summary = "Solicitar capitanía", description = "Un jugador solicita al capitán actual asumir la capitanía del equipo. El capitán recibe una notificación.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud enviada al capitán"),
            @ApiResponse(responseCode = "400", description = "Ya existe una solicitud pendiente o el jugador ya es capitán"),
            @ApiResponse(responseCode = "403", description = "El jugador debe ser miembro del equipo"),
            @ApiResponse(responseCode = "404", description = "Equipo no encontrado")
    })
    public ResponseEntity<CaptaincyTransferResponse> applyForCaptaincy(
            @PathVariable String teamId,
            Authentication authentication) {

        String playerId = (String) authentication.getPrincipal();
        String token = (String) authentication.getCredentials();
        String playerName = jwtUtil.extractFullName(token);

        CaptaincyTransferRequest transfer = transferCaptaincyUseCase.applyForCaptaincy(
                playerId, playerName != null ? playerName : "Player", teamId);

        return ResponseEntity.status(HttpStatus.CREATED).body(transferMapper.toResponse(transfer));
    }

    @PutMapping("/{transferId}/respond")
    @Operation(summary = "Responder solicitud de transferencia", description = "El jugador designado acepta o rechaza la solicitud de transferencia de capitanía.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud ya no está pendiente"),
            @ApiResponse(responseCode = "403", description = "Solo el destinatario puede responder"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada")
    })
    public ResponseEntity<CaptaincyTransferResponse> respondToTransfer(
            @PathVariable String transferId,
            @RequestBody @Valid TransferResponseRequest request,
            Authentication authentication) {

        String userId = (String) authentication.getPrincipal();

        CaptaincyTransferRequest transfer = transferCaptaincyUseCase.respondToTransfer(
                userId, transferId, request.getAccept());

        return ResponseEntity.ok(transferMapper.toResponse(transfer));
    }
}
