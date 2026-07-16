package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.port.in.ConsultTournamentTeamsUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.in.EnrollTeamInTournamentUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.RegisteredTeamResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.TournamentEnrollmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tournaments")
@RequiredArgsConstructor
@Tag(name = "Torneos", description = "Consulta e inscripcion de equipos en torneos (proxy hacia mk-tournament-service)")
public class TournamentController {

    private final ConsultTournamentTeamsUseCase consultTournamentTeamsUseCase;
    private final EnrollTeamInTournamentUseCase enrollTeamInTournamentUseCase;

    @GetMapping("/{tournamentId}/teams")
    @Operation(summary = "TC-21: Consultar equipos de un torneo", description = "Lista los equipos registrados en un torneo, consultando a Tournament Service.")
    public ResponseEntity<List<RegisteredTeamResponse>> getRegisteredTeams(@PathVariable UUID tournamentId) {
        List<RegisteredTeamResponse> result = consultTournamentTeamsUseCase.getRegisteredTeams(tournamentId).stream()
                .map(t -> new RegisteredTeamResponse(t.teamId(), t.teamName(), t.registrationStatus(), t.logoUrl()))
                .toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{tournamentId}/teams/{teamId}/enrollment")
    @Operation(summary = "TC-24: Inscribir equipo a torneo", description = "El capitan inscribe su equipo en un torneo. Requiere al menos 7 miembros; Tournament Service valida cupos y estado del torneo.")
    public ResponseEntity<TournamentEnrollmentResponse> enrollTeam(
            @PathVariable UUID tournamentId,
            @PathVariable UUID teamId,
            Authentication authentication) {

        UUID captainId = (UUID) authentication.getPrincipal();

        TournamentServicePort.Enrollment enrollment =
                enrollTeamInTournamentUseCase.enrollTeam(captainId, teamId, tournamentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(new TournamentEnrollmentResponse(
                enrollment.enrollmentId(), enrollment.status(), enrollment.reservationExpiresAt()));
    }
}
