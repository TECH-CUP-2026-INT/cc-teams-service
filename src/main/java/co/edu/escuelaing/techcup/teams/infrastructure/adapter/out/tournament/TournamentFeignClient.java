package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament;

import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.EnrollTeamRequestDTO;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.EnrollmentResponseDTO;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.RegisteredTeamResponseDTO;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.TeamActiveEnrollmentResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "tournament-service", url = "${tournament.service.base-url}")
public interface TournamentFeignClient {

    @GetMapping("/tournaments/{tournamentId}/teams")
    List<RegisteredTeamResponseDTO> getRegisteredTeams(@PathVariable("tournamentId") UUID tournamentId);

    @PostMapping("/tournaments/{tournamentId}/enrollments")
    EnrollmentResponseDTO enrollTeam(@PathVariable("tournamentId") UUID tournamentId,
                                      @RequestBody EnrollTeamRequestDTO request);

    /**
     * Contrato asumido — ver {@link TeamActiveEnrollmentResponseDTO}. Hoy
     * Tournament Service no expone esta ruta; cualquier llamada fallará con
     * FeignException hasta que la reconstruyan.
     */
    @GetMapping("/tournaments/by-team/{teamId}/active")
    TeamActiveEnrollmentResponseDTO hasActiveEnrollment(@PathVariable("teamId") UUID teamId);
}
