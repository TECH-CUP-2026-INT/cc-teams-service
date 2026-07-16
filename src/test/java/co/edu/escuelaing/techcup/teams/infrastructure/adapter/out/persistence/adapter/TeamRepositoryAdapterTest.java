package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.model.Team;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.TeamDocument;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.TeamMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.TeamMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamRepositoryAdapterTest {

    @Mock
    private TeamMongoRepository mongoRepository;
    @Mock
    private TeamMapper mapper;

    private TeamRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TeamRepositoryAdapter(mongoRepository, mapper);
    }

    @Test
    void generatesIdWhenSavingANewTeam() {
        Team team = Team.builder().name("Los Halcones FC").build();
        TeamDocument document = TeamDocument.builder().build();
        when(mapper.toDocument(any(Team.class))).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(team);

        adapter.save(team);

        assertThat(team.getId()).isNotNull();
    }

    @Test
    void keepsExistingIdWhenSavingAnAlreadyPersistedTeam() {
        UUID existingId = UUID.randomUUID();
        Team team = Team.builder().id(existingId).name("Los Halcones FC").build();
        TeamDocument document = TeamDocument.builder().build();
        when(mapper.toDocument(team)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(team);

        adapter.save(team);

        assertThat(team.getId()).isEqualTo(existingId);
    }

    @Test
    void findByIdDelegatesToMongoRepository() {
        UUID id = UUID.randomUUID();
        TeamDocument document = TeamDocument.builder().id(id).build();
        Team team = Team.builder().id(id).build();
        when(mongoRepository.findById(id)).thenReturn(Optional.of(document));
        when(mapper.toDomain(document)).thenReturn(team);

        Optional<Team> result = adapter.findById(id);

        assertThat(result).contains(team);
    }

    @Test
    void findByCaptainIdDelegatesToMongoRepository() {
        UUID captainId = UUID.randomUUID();
        TeamDocument document = TeamDocument.builder().captainId(captainId).build();
        Team team = Team.builder().captainId(captainId).build();
        when(mongoRepository.findByCaptainId(captainId)).thenReturn(Optional.of(document));
        when(mapper.toDomain(document)).thenReturn(team);

        assertThat(adapter.findByCaptainId(captainId)).contains(team);
    }

    @Test
    void existsByNameDelegatesToMongoRepository() {
        when(mongoRepository.existsByName("Los Halcones FC")).thenReturn(true);

        assertThat(adapter.existsByName("Los Halcones FC")).isTrue();
    }
}
