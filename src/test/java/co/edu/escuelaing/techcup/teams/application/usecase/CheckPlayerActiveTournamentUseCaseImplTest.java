package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import co.edu.escuelaing.techcup.teams.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckPlayerActiveTournamentUseCaseImplTest {

    @Mock
    private TeamRepositoryPort teamRepository;
    @Mock
    private TournamentServicePort tournamentServicePort;

    @InjectMocks
    private CheckPlayerActiveTournamentUseCaseImpl useCase;

    @Test
    void retornaTrueSiElEquipoDelJugadorTieneTorneoActivo() {
        when(teamRepository.findByMembersUserId(TestFixtures.PLAYER_ID)).thenReturn(Optional.of(TestFixtures.team()));
        when(tournamentServicePort.hasActiveEnrollment(TestFixtures.TEAM_ID)).thenReturn(true);

        assertThat(useCase.hasActiveTournament(TestFixtures.PLAYER_ID)).isTrue();
    }

    @Test
    void retornaFalseSiElEquipoNoTieneTorneoActivo() {
        when(teamRepository.findByMembersUserId(TestFixtures.PLAYER_ID)).thenReturn(Optional.of(TestFixtures.team()));
        when(tournamentServicePort.hasActiveEnrollment(TestFixtures.TEAM_ID)).thenReturn(false);

        assertThat(useCase.hasActiveTournament(TestFixtures.PLAYER_ID)).isFalse();
    }

    @Test
    void retornaFalseSiElJugadorNoTieneEquipoSinConsultarTournament() {
        when(teamRepository.findByMembersUserId(TestFixtures.NON_MEMBER_ID)).thenReturn(Optional.empty());

        assertThat(useCase.hasActiveTournament(TestFixtures.NON_MEMBER_ID)).isFalse();
        verify(tournamentServicePort, never()).hasActiveEnrollment(org.mockito.ArgumentMatchers.any());
    }
}
