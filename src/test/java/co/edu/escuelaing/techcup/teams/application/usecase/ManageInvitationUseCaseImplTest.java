package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.exception.PlayerAlreadyInTeamException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.UnauthorizedTeamActionException;
import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.NotificationPort;
import co.edu.escuelaing.techcup.teams.domain.port.out.TeamInvitationRepositoryPort;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageInvitationUseCaseImplTest {

    @Mock
    private TeamRepositoryPort teamRepository;
    @Mock
    private TeamInvitationRepositoryPort invitationRepository;
    @Mock
    private AuditEventRepositoryPort auditRepository;
    @Mock
    private NotificationPort notificationPort;

    @InjectMocks
    private ManageInvitationUseCaseImpl useCase;

    @Test
    void sendInvitationSuccessfully() {
        Team team = TestFixtures.teamWithOnlyCaptain();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));
        when(invitationRepository.findByTeamIdAndInvitedUserIdAndStatus(
                eq(TestFixtures.TEAM_ID), eq("new-player"), eq(InvitationStatus.PENDING)))
                .thenReturn(Optional.empty());
        when(invitationRepository.save(any(TeamInvitation.class))).thenAnswer(inv -> {
            TeamInvitation i = inv.getArgument(0);
            i.setId(TestFixtures.INVITATION_ID);
            return i;
        });

        TeamInvitation result = useCase.sendInvitation(TestFixtures.CAPTAIN_ID, TestFixtures.TEAM_ID, "new-player");

        assertThat(result.getStatus()).isEqualTo(InvitationStatus.PENDING);
        verify(notificationPort).publishTeamInvitation(anyString(), anyString(), anyString(), anyString(), anyString());
        verify(auditRepository).save(any());
    }

    @Test
    void sendInvitationThrowsWhenNotCaptain() {
        Team team = TestFixtures.team();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));

        assertThatThrownBy(() -> useCase.sendInvitation("other-user", TestFixtures.TEAM_ID, "new-player"))
                .isInstanceOf(UnauthorizedTeamActionException.class);
    }

    @Test
    void sendInvitationThrowsWhenPlayerAlreadyMember() {
        Team team = TestFixtures.team();
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));

        assertThatThrownBy(() -> useCase.sendInvitation(TestFixtures.CAPTAIN_ID, TestFixtures.TEAM_ID, TestFixtures.PLAYER_ID))
                .isInstanceOf(PlayerAlreadyInTeamException.class);
    }

    @Test
    void sendInvitationThrowsWhenTeamNotFound() {
        when(teamRepository.findById("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.sendInvitation(TestFixtures.CAPTAIN_ID, "nonexistent", "new-player"))
                .isInstanceOf(TeamNotFoundException.class);
    }

    @Test
    void respondToInvitationAcceptSuccessfully() {
        TeamInvitation invitation = TestFixtures.pendingInvitation();
        Team team = TestFixtures.teamWithOnlyCaptain();
        when(invitationRepository.findById(TestFixtures.INVITATION_ID)).thenReturn(Optional.of(invitation));
        when(teamRepository.findById(TestFixtures.TEAM_ID)).thenReturn(Optional.of(team));
        when(teamRepository.findByMembersUserId("new-player-1")).thenReturn(Optional.empty());
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TeamInvitation result = useCase.respondToInvitation("new-player-1", "New Player", TestFixtures.INVITATION_ID, true);

        assertThat(result.getStatus()).isEqualTo(InvitationStatus.ACCEPTED);
        verify(teamRepository).save(any(Team.class));
    }

    @Test
    void respondToInvitationRejectSuccessfully() {
        TeamInvitation invitation = TestFixtures.pendingInvitation();
        when(invitationRepository.findById(TestFixtures.INVITATION_ID)).thenReturn(Optional.of(invitation));
        when(invitationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TeamInvitation result = useCase.respondToInvitation("new-player-1", "New Player", TestFixtures.INVITATION_ID, false);

        assertThat(result.getStatus()).isEqualTo(InvitationStatus.REJECTED);
    }
}
