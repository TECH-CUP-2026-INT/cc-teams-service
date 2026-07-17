package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotEligibleForTournamentException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.UnauthorizedTeamActionException;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TournamentServicePort;
import co.edu.escuelaing.techcup.teams.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnrollTeamInTournamentUseCaseImplTest {

    private static final UUID TOURNAMENT_ID = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");

    @Mock
    private TeamRepositoryPort teamRepository;
    @Mock
    private TournamentServicePort tournamentServicePort;
    @Mock
    private AuditEventRepositoryPort auditRepository;

    @InjectMocks
    private EnrollTeamInTournamentUseCaseImpl useCase;

    @Test
    void enrollsSuccessfullyWhenCaptainAndEligible() {
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(TestFixtures.teamEligibleForTournament()));
        TournamentServicePort.Enrollment enrollment = new TournamentServicePort.Enrollment(
                UUID.randomUUID(), "RESERVED", LocalDateTime.now());
        when(tournamentServicePort.enrollTeam(TOURNAMENT_ID, TestFixtures.TEAM_ID)).thenReturn(enrollment);

        TournamentServicePort.Enrollment result = useCase.enrollTeam(TestFixtures.CAPTAIN_ID, TestFixtures.TEAM_ID, TOURNAMENT_ID);

        assertThat(result).isEqualTo(enrollment);
        verify(auditRepository).save(any());
    }

    @Test
    void throwsWhenTeamNotFound() {
        when(teamRepository.findById(TestFixtures.NONEXISTENT_TEAM_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.enrollTeam(TestFixtures.CAPTAIN_ID, TestFixtures.NONEXISTENT_TEAM_ID, TOURNAMENT_ID))
                .isInstanceOf(TeamNotFoundException.class);
    }

    @Test
    void throwsWhenCallerIsNotCaptain() {
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(TestFixtures.teamEligibleForTournament()));

        assertThatThrownBy(() -> useCase.enrollTeam(TestFixtures.PLAYER_ID, TestFixtures.TEAM_ID, TOURNAMENT_ID))
                .isInstanceOf(UnauthorizedTeamActionException.class);
    }

    @Test
    void throwsWhenRosterTooSmall() {
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(TestFixtures.team()));

        assertThatThrownBy(() -> useCase.enrollTeam(TestFixtures.CAPTAIN_ID, TestFixtures.TEAM_ID, TOURNAMENT_ID))
                .isInstanceOf(TeamNotEligibleForTournamentException.class);
    }
}
