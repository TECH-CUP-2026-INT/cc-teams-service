package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.CaptaincyTransferDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaptaincyTransferMongoRepository extends MongoRepository<CaptaincyTransferDocument, UUID> {
    Optional<CaptaincyTransferDocument> findByTeamIdAndStatus(UUID teamId, TransferRequestStatus status);
}
