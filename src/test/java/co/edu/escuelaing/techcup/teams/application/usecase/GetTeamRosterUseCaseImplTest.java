package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.port.in.GetTeamRosterUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTeamRosterUseCaseImplTest {

    @Mock
    private TeamRepositoryPort teamRepository;

    @InjectMocks
    private GetTeamRosterUseCaseImpl useCase;

    @Test
    void returnsTeamIdAndMemberIdsWhenPlayerBelongsToATeam() {
        when(teamRepository.findByMembersUserId(TestFixtures.PLAYER_ID)).thenReturn(Optional.of(TestFixtures.team()));

        Optional<GetTeamRosterUseCase.TeamRoster> result = useCase.getTeamRosterByPlayer(TestFixtures.PLAYER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().teamId()).isEqualTo(TestFixtures.TEAM_ID);
        assertThat(result.get().memberIds()).containsExactlyInAnyOrder(TestFixtures.CAPTAIN_ID, TestFixtures.PLAYER_ID);
    }

    @Test
    void returnsEmptyWhenPlayerHasNoTeam() {
        when(teamRepository.findByMembersUserId(TestFixtures.NON_MEMBER_ID)).thenReturn(Optional.empty());

        Optional<GetTeamRosterUseCase.TeamRoster> result = useCase.getTeamRosterByPlayer(TestFixtures.NON_MEMBER_ID);

        assertThat(result).isEmpty();
    }
}
