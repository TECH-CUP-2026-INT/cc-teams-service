package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.AuditEventDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface AuditEventMongoRepository extends MongoRepository<AuditEventDocument, UUID> {
}
