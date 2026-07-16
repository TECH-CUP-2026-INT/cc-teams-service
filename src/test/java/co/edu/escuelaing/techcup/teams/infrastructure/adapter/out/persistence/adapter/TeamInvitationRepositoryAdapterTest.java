package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.enums.InvitationStatus;
import co.edu.escuelaing.techcup.teams.domain.model.TeamInvitation;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamInvitationDocument;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.TeamInvitationMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamInvitationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamInvitationRepositoryAdapterTest {

    @Mock
    private TeamInvitationMongoRepository mongoRepository;
    @Mock
    private TeamInvitationMapper mapper;

    private TeamInvitationRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TeamInvitationRepositoryAdapter(mongoRepository, mapper);
    }

    @Test
    void generatesIdWhenSavingANewInvitation() {
        TeamInvitation invitation = TeamInvitation.builder().teamName("Los Halcones FC").build();
        TeamInvitationDocument document = TeamInvitationDocument.builder().build();
        when(mapper.toDocument(any(TeamInvitation.class))).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(invitation);

        adapter.save(invitation);

        assertThat(invitation.getId()).isNotNull();
    }

    @Test
    void keepsExistingIdWhenSavingAnAlreadyPersistedInvitation() {
        UUID existingId = UUID.randomUUID();
        TeamInvitation invitation = TeamInvitation.builder().id(existingId).build();
        TeamInvitationDocument document = TeamInvitationDocument.builder().build();
        when(mapper.toDocument(invitation)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(invitation);

        adapter.save(invitation);

        assertThat(invitation.getId()).isEqualTo(existingId);
    }

    @Test
    void findByInvitedUserIdDelegatesToMongoRepository() {
        UUID userId = UUID.randomUUID();
        TeamInvitationDocument document = TeamInvitationDocument.builder().invitedUserId(userId).build();
        TeamInvitation invitation = TeamInvitation.builder().invitedUserId(userId).build();
        when(mongoRepository.findByInvitedUserId(userId)).thenReturn(List.of(document));
        when(mapper.toDomain(document)).thenReturn(invitation);

        assertThat(adapter.findByInvitedUserId(userId)).containsExactly(invitation);
    }

    @Test
    void findByTeamIdDelegatesToMongoRepository() {
        UUID teamId = UUID.randomUUID();
        TeamInvitationDocument document = TeamInvitationDocument.builder().teamId(teamId).build();
        TeamInvitation invitation = TeamInvitation.builder().teamId(teamId).build();
        when(mongoRepository.findByTeamId(teamId)).thenReturn(List.of(document));
        when(mapper.toDomain(document)).thenReturn(invitation);

        assertThat(adapter.findByTeamId(teamId)).containsExactly(invitation);
    }

    @Test
    void findByTeamIdAndInvitedUserIdAndStatusDelegatesToMongoRepository() {
        UUID teamId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        TeamInvitationDocument document = TeamInvitationDocument.builder().teamId(teamId).invitedUserId(userId).build();
        TeamInvitation invitation = TeamInvitation.builder().teamId(teamId).invitedUserId(userId).build();
        when(mongoRepository.findByTeamIdAndInvitedUserIdAndStatus(teamId, userId, InvitationStatus.PENDING))
                .thenReturn(Optional.of(document));
        when(mapper.toDomain(document)).thenReturn(invitation);

        assertThat(adapter.findByTeamIdAndInvitedUserIdAndStatus(teamId, userId, InvitationStatus.PENDING))
                .contains(invitation);
    }
}
