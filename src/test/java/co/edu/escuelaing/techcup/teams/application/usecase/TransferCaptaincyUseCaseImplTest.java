package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.exception.InvalidTransferStateException;
import co.edu.escuelaing.techcup.teams.domain.exception.UnauthorizedTeamActionException;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.CaptaincyTransferRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.NotificationPort;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferCaptaincyUseCaseImplTest {

    @Mock
    private TeamRepositoryPort teamRepository;
    @Mock
    private CaptaincyTransferRepositoryPort transferRepository;
    @Mock
    private AuditEventRepositoryPort auditRepository;
    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private TransferCaptaincyUseCaseImpl useCase;

    @Test
    void initiateTransferSuccessfully() {
        Team team = TestFixtures.team();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));
        when(transferRepository.findByTeamIdAndStatus(eq(TestFixtures.TEAM_ID), eq(TransferRequestStatus.PENDING)))
                .thenReturn(Optional.empty());
        when(transferRepository.save(any())).thenAnswer(inv -> {
            CaptaincyTransferRequest r = inv.getArgument(0);
            r.setId(TestFixtures.TRANSFER_ID);
            return r;
        });

        CaptaincyTransferRequest result = useCase.initiateTransfer(
                TestFixtures.CAPTAIN_ID, TestFixtures.TEAM_ID, TestFixtures.PLAYER_ID);

        assertThat(result.getStatus()).isEqualTo(TransferRequestStatus.PENDING);
        assertThat(result.getInitiatedBy()).isEqualTo("CAPTAIN");
        verify(notificationPort).publishCaptaincyTransfer(any(), any(), any(), any(), any());
    }

    @Test
    void initiateTransferThrowsWhenNotCaptain() {
        Team team = TestFixtures.team();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));

        assertThatThrownBy(() -> useCase.initiateTransfer("other-user", TestFixtures.TEAM_ID, TestFixtures.PLAYER_ID))
                .isInstanceOf(UnauthorizedTeamActionException.class);
    }

    @Test
    void initiateTransferThrowsWhenTargetNotMember() {
        Team team = TestFixtures.team();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));

        assertThatThrownBy(() -> useCase.initiateTransfer(TestFixtures.CAPTAIN_ID, TestFixtures.TEAM_ID, "non-member"))
                .isInstanceOf(UnauthorizedTeamActionException.class);
    }

    @Test
    void applyForCaptaincySuccessfully() {
        Team team = TestFixtures.team();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));
        when(transferRepository.findByTeamIdAndStatus(eq(TestFixtures.TEAM_ID), eq(TransferRequestStatus.PENDING)))
                .thenReturn(Optional.empty());
        when(transferRepository.save(any())).thenAnswer(inv -> {
            CaptaincyTransferRequest r = inv.getArgument(0);
            r.setId(TestFixtures.TRANSFER_ID);
            return r;
        });

        CaptaincyTransferRequest result = useCase.applyForCaptaincy(
                TestFixtures.PLAYER_ID, TestFixtures.PLAYER_NAME, TestFixtures.TEAM_ID);

        assertThat(result.getInitiatedBy()).isEqualTo("PLAYER");
        verify(notificationPort).publishTeamLinkRequest(any(), any(), any(), any(), any(), any());
    }

    @Test
    void applyForCaptaincyThrowsWhenAlreadyCaptain() {
        Team team = TestFixtures.team();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));

        assertThatThrownBy(() -> useCase.applyForCaptaincy(TestFixtures.CAPTAIN_ID, TestFixtures.CAPTAIN_NAME, TestFixtures.TEAM_ID))
                .isInstanceOf(InvalidTransferStateException.class);
    }

    @Test
    void respondToTransferAcceptSuccessfully() {
        CaptaincyTransferRequest transfer = TestFixtures.pendingTransfer();
        Team team = TestFixtures.team();
        when(transferRepository.findById(TestFixtures.TRANSFER_ID)).thenReturn(Optional.of(transfer));
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CaptaincyTransferRequest result = useCase.respondToTransfer(
                TestFixtures.PLAYER_ID, TestFixtures.TRANSFER_ID, true);

        assertThat(result.getStatus()).isEqualTo(TransferRequestStatus.ACCEPTED);
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void respondToTransferRejectSuccessfully() {
        CaptaincyTransferRequest transfer = TestFixtures.pendingTransfer();
        when(transferRepository.findById(TestFixtures.TRANSFER_ID)).thenReturn(Optional.of(transfer));
        when(transferRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CaptaincyTransferRequest result = useCase.respondToTransfer(
                TestFixtures.PLAYER_ID, TestFixtures.TRANSFER_ID, false);

        assertThat(result.getStatus()).isEqualTo(TransferRequestStatus.REJECTED);
    }
}
