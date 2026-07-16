package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamInfoUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTeamInfoUseCaseImplTest {

    @Mock
    private TeamRepositoryPort teamRepository;

    @InjectMocks
    private GetTeamInfoUseCaseImpl useCase;

    @Test
    void returnsTeamNameAndRosterSize() {
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(TestFixtures.team()));

        GetTeamInfoUseCase.TeamInfo result = useCase.getTeamInfo(TestFixtures.TEAM_ID);

        assertThat(result.teamName()).isEqualTo(TestFixtures.TEAM_NAME);
        assertThat(result.rosterSize()).isEqualTo(2);
    }

    @Test
    void throwsWhenTeamNotFound() {
        when(teamRepository.findById(TestFixtures.NONEXISTENT_TEAM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getTeamInfo(TestFixtures.NONEXISTENT_TEAM_ID))
                .isInstanceOf(TeamNotFoundException.class);
    }
}
