package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament;

import co.edu.escuelaing.techcup.teams.domain.exception.TournamentEnrollmentRejectedException;
import co.edu.escuelaing.techcup.teams.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.TournamentServiceUnavailableException;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.EnrollTeamRequestDTO;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.EnrollmentResponseDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class TournamentServiceAdapter implements TournamentServicePort {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final TournamentFeignClient tournamentFeignClient;

    public TournamentServiceAdapter(TournamentFeignClient tournamentFeignClient) {
        this.tournamentFeignClient = tournamentFeignClient;
    }

    @Override
    public List<TournamentTeam> getRegisteredTeams(UUID tournamentId) {
        try {
            return tournamentFeignClient.getRegisteredTeams(tournamentId).stream()
                    .map(dto -> new TournamentTeam(dto.teamId(), dto.teamName(), dto.registrationStatus(), dto.logoUrl()))
                    .toList();
        } catch (FeignException.NotFound e) {
            throw new TournamentNotFoundException(tournamentId);
        } catch (FeignException e) {
            throw new TournamentServiceUnavailableException(tournamentId, e);
        }
    }

    @Override
    public Enrollment enrollTeam(UUID tournamentId, UUID teamId) {
        try {
            EnrollmentResponseDTO response = tournamentFeignClient.enrollTeam(tournamentId, new EnrollTeamRequestDTO(teamId));
            return new Enrollment(response.enrollmentId(), response.status(), response.reservationExpiresAt());
        } catch (FeignException.NotFound e) {
            throw new TournamentNotFoundException(tournamentId);
        } catch (FeignException.Conflict | FeignException.BadRequest e) {
            throw new TournamentEnrollmentRejectedException(extractMessage(e));
        } catch (FeignException e) {
            throw new TournamentServiceUnavailableException(tournamentId, e);
        }
    }

    @Override
    public boolean hasActiveEnrollment(UUID teamId) {
        try {
            return tournamentFeignClient.hasActiveEnrollment(teamId).hasActiveEnrollment();
        } catch (FeignException e) {
            log.warn("No se pudo consultar el estado de torneo activo del equipo {} en Tournament Service " +
                    "(endpoint pendiente de reconstruir en ese repo); se asume sin torneo activo. Causa: {}",
                    teamId, e.getMessage());
            return false;
        }
    }

    private String extractMessage(FeignException e) {
        try {
            JsonNode node = OBJECT_MAPPER.readTree(e.contentUTF8());
            if (node.hasNonNull("message")) {
                return node.get("message").asText();
            }
        } catch (Exception ignored) {
            // fall through to generic message below
        }
        return "Tournament Service rejected the enrollment request";
    }
}
