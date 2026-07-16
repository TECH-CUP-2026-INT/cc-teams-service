package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament;

import co.edu.escuelaing.techcup.teams.domain.exception.TournamentEnrollmentRejectedException;
import co.edu.escuelaing.techcup.teams.domain.exception.TournamentNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.TournamentServiceUnavailableException;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.EnrollmentResponseDTO;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.tournament.dto.RegisteredTeamResponseDTO;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TournamentServiceAdapterTest {

    private static final UUID TOURNAMENT_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
    private static final UUID TEAM_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private TournamentFeignClient feignClient;

    private TournamentServiceAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TournamentServiceAdapter(feignClient);
    }

    @Test
    void getRegisteredTeamsMapsResponse() {
        when(feignClient.getRegisteredTeams(TOURNAMENT_ID)).thenReturn(
                List.of(new RegisteredTeamResponseDTO(TEAM_ID, "Los Halcones FC", "APPROVED", "https://logo")));

        List<TournamentServicePort.TournamentTeam> result = adapter.getRegisteredTeams(TOURNAMENT_ID);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).teamName()).isEqualTo("Los Halcones FC");
    }

    @Test
    void getRegisteredTeamsThrowsNotFoundWhenTournamentMissing() {
        when(feignClient.getRegisteredTeams(TOURNAMENT_ID)).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> adapter.getRegisteredTeams(TOURNAMENT_ID))
                .isInstanceOf(TournamentNotFoundException.class);
    }

    @Test
    void getRegisteredTeamsThrowsUnavailableOnOtherFailures() {
        when(feignClient.getRegisteredTeams(TOURNAMENT_ID)).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> adapter.getRegisteredTeams(TOURNAMENT_ID))
                .isInstanceOf(TournamentServiceUnavailableException.class);
    }

    @Test
    void enrollTeamMapsResponse() {
        when(feignClient.enrollTeam(any(), any())).thenReturn(
                new EnrollmentResponseDTO(UUID.randomUUID(), "RESERVED", null));

        TournamentServicePort.Enrollment result = adapter.enrollTeam(TOURNAMENT_ID, TEAM_ID);

        assertThat(result.status()).isEqualTo("RESERVED");
    }

    @Test
    void enrollTeamThrowsNotFoundWhenTournamentMissing() {
        when(feignClient.enrollTeam(any(), any())).thenThrow(mock(FeignException.NotFound.class));

        assertThatThrownBy(() -> adapter.enrollTeam(TOURNAMENT_ID, TEAM_ID))
                .isInstanceOf(TournamentNotFoundException.class);
    }

    @Test
    void enrollTeamThrowsRejectedOnConflictWithMessageFromBody() {
        FeignException.Conflict conflict = mock(FeignException.Conflict.class);
        when(conflict.contentUTF8()).thenReturn("{\"message\":\"Tournament is not open for enrollment\"}");
        when(feignClient.enrollTeam(any(), any())).thenThrow(conflict);

        assertThatThrownBy(() -> adapter.enrollTeam(TOURNAMENT_ID, TEAM_ID))
                .isInstanceOf(TournamentEnrollmentRejectedException.class)
                .hasMessage("Tournament is not open for enrollment");
    }

    @Test
    void enrollTeamThrowsRejectedOnBadRequestWithFallbackMessage() {
        FeignException.BadRequest badRequest = mock(FeignException.BadRequest.class);
        when(badRequest.contentUTF8()).thenReturn("not-json");
        when(feignClient.enrollTeam(any(), any())).thenThrow(badRequest);

        assertThatThrownBy(() -> adapter.enrollTeam(TOURNAMENT_ID, TEAM_ID))
                .isInstanceOf(TournamentEnrollmentRejectedException.class)
                .hasMessage("Tournament Service rejected the enrollment request");
    }

    @Test
    void enrollTeamThrowsUnavailableOnOtherFailures() {
        when(feignClient.enrollTeam(any(), any())).thenThrow(mock(FeignException.class));

        assertThatThrownBy(() -> adapter.enrollTeam(TOURNAMENT_ID, TEAM_ID))
                .isInstanceOf(TournamentServiceUnavailableException.class);
    }
}
