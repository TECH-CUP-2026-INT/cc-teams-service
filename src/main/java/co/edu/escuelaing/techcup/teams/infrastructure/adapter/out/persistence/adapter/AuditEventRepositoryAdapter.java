package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.adapter;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.AuditEventDocument;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository.AuditEventMongoRepository;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.AuditEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuditEventRepositoryAdapter implements AuditEventRepositoryPort {

    private static final String TIMESTAMP_FIELD = "timestamp";

    private final AuditEventMongoRepository mongoRepository;
    private final MongoTemplate mongoTemplate;
    private final AuditEventMapper mapper;

    @Override
    public AuditEvent save(AuditEvent event) {
        if (event.getId() == null) {
            event.setId(UUID.randomUUID());
        }
        var document = mapper.toDocument(event);
        var saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }

    @Override
    public List<AuditEvent> findByFilters(LocalDateTime startDate, LocalDateTime endDate,
                                          AuditActionType actionType, UUID teamId) {
        Query query = new Query();

        if (startDate != null && endDate != null) {
            query.addCriteria(Criteria.where(TIMESTAMP_FIELD).gte(startDate).lte(endDate));
        } else if (startDate != null) {
            query.addCriteria(Criteria.where(TIMESTAMP_FIELD).gte(startDate));
        } else if (endDate != null) {
            query.addCriteria(Criteria.where(TIMESTAMP_FIELD).lte(endDate));
        }
        if (actionType != null) {
            query.addCriteria(Criteria.where("actionType").is(actionType));
        }
        if (teamId != null) {
            query.addCriteria(Criteria.where("teamId").is(teamId));
        }

        return mongoTemplate.find(query, AuditEventDocument.class).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
