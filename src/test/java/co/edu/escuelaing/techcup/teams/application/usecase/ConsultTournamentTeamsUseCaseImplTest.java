package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultTournamentTeamsUseCaseImplTest {

    private static final UUID TOURNAMENT_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    @Mock
    private TournamentServicePort tournamentServicePort;

    @InjectMocks
    private ConsultTournamentTeamsUseCaseImpl useCase;

    @Test
    void delegatesToTournamentServicePort() {
        TournamentServicePort.TournamentTeam team = new TournamentServicePort.TournamentTeam(
                UUID.randomUUID(), "Los Halcones FC", "APPROVED", "https://placeholder.com/logo");
        when(tournamentServicePort.getRegisteredTeams(TOURNAMENT_ID)).thenReturn(List.of(team));

        List<TournamentServicePort.TournamentTeam> result = useCase.getRegisteredTeams(TOURNAMENT_ID);

        assertThat(result).containsExactly(team);
    }
}
