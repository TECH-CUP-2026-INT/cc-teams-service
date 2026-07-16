package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.AuditEventDocument;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.AuditEventMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.AuditEventMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditEventRepositoryAdapterTest {

    @Mock
    private AuditEventMongoRepository mongoRepository;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private AuditEventMapper mapper;

    private AuditEventRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AuditEventRepositoryAdapter(mongoRepository, mongoTemplate, mapper);
    }

    @Test
    void generatesIdWhenSavingANewEvent() {
        AuditEvent event = AuditEvent.builder().actionType(AuditActionType.TEAM_CREATED).build();
        AuditEventDocument document = AuditEventDocument.builder().build();
        when(mapper.toDocument(any(AuditEvent.class))).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(event);

        adapter.save(event);

        assertThat(event.getId()).isNotNull();
    }

    @Test
    void keepsExistingIdWhenSavingAnAlreadyPersistedEvent() {
        UUID existingId = UUID.randomUUID();
        AuditEvent event = AuditEvent.builder().id(existingId).build();
        AuditEventDocument document = AuditEventDocument.builder().build();
        when(mapper.toDocument(event)).thenReturn(document);
        when(mongoRepository.save(document)).thenReturn(document);
        when(mapper.toDomain(document)).thenReturn(event);

        adapter.save(event);

        assertThat(event.getId()).isEqualTo(existingId);
    }

    @Test
    void findByFiltersDelegatesToMongoTemplateAndMapsResults() {
        UUID teamId = UUID.randomUUID();
        AuditEventDocument document = AuditEventDocument.builder().teamId(teamId).build();
        AuditEvent event = AuditEvent.builder().teamId(teamId).build();
        when(mongoTemplate.find(any(Query.class), org.mockito.ArgumentMatchers.eq(AuditEventDocument.class)))
                .thenReturn(List.of(document));
        when(mapper.toDomain(document)).thenReturn(event);

        List<AuditEvent> result = adapter.findByFilters(
                LocalDateTime.now().minusDays(1), LocalDateTime.now(), AuditActionType.TEAM_CREATED, teamId);

        assertThat(result).containsExactly(event);
    }

    @Test
    void findByFiltersWorksWithAllFiltersNull() {
        when(mongoTemplate.find(any(Query.class), org.mockito.ArgumentMatchers.eq(AuditEventDocument.class)))
                .thenReturn(List.of());

        List<AuditEvent> result = adapter.findByFilters(null, null, null, null);

        assertThat(result).isEmpty();
    }
}
