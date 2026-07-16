package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.exception.DomainException;
import co.edu.escuelaing.techcup.teams.domain.port.in.AuditQueryUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.AuditEventMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Auditoría", description = "Consulta de eventos de auditoría del servicio de equipos")
public class AuditController {

    private final AuditQueryUseCase auditQueryUseCase;
    private final AuditEventMapper auditEventMapper;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Consultar eventos de auditoría", description = "Permite al Admin consultar el log de auditoría del servicio de equipos con filtros opcionales por fecha, tipo de acción y equipo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de eventos de auditoría"),
            @ApiResponse(responseCode = "400", description = "Rango de fechas inválido"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Solo accesible por Admin")
    })
    public ResponseEntity<List<AuditEventResponse>> queryEvents(
            @Parameter(description = "Fecha de inicio del rango (formato ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Fecha de fin del rango (formato ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Tipo de acción de auditoría")
            @RequestParam(required = false) AuditActionType actionType,
            @Parameter(description = "ID del equipo")
            @RequestParam(required = false) UUID teamId) {

        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new DomainException("INVALID_DATE_RANGE", "startDate must be before or equal to endDate");
        }

        List<AuditEventResponse> responses = auditQueryUseCase.queryEvents(startDate, endDate, actionType, teamId)
                .stream()
                .map(auditEventMapper::toResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
