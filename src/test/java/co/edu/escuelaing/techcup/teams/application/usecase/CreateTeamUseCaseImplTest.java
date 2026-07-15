package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.exception.TeamNameAlreadyExistsException;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateTeamUseCaseImplTest {

    @Mock
    private TeamRepositoryPort teamRepository;
    @Mock
    private AuditEventRepositoryPort auditRepository;

    @InjectMocks
    private CreateTeamUseCaseImpl useCase;

    @Test
    void createTeamSuccessfully() {
        when(teamRepository.existsByName(TestFixtures.TEAM_NAME)).thenReturn(false);
        when(teamRepository.save(any(Team.class))).thenAnswer(inv -> {
            Team t = inv.getArgument(0);
            t.setId(TestFixtures.TEAM_ID);
            return t;
        });

        Team result = useCase.createTeam(TestFixtures.CAPTAIN_ID, TestFixtures.CAPTAIN_NAME,
                TestFixtures.TEAM_NAME, new byte[]{1}, "image/png", "#FF0000");

        assertThat(result.getId()).isEqualTo(TestFixtures.TEAM_ID);
        assertThat(result.getName()).isEqualTo(TestFixtures.TEAM_NAME);
        assertThat(result.getCaptainId()).isEqualTo(TestFixtures.CAPTAIN_ID);
        assertThat(result.getMembers()).hasSize(1);
        verify(auditRepository).save(any());
    }

    @Test
    void createTeamThrowsWhenNameExists() {
        when(teamRepository.existsByName(TestFixtures.TEAM_NAME)).thenReturn(true);

        assertThatThrownBy(() -> useCase.createTeam(TestFixtures.CAPTAIN_ID, TestFixtures.CAPTAIN_NAME,
                TestFixtures.TEAM_NAME, new byte[]{1}, "image/png", null))
                .isInstanceOf(TeamNameAlreadyExistsException.class);
    }
}
