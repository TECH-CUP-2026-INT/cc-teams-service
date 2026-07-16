package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.CaptaincyTransferDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface CaptaincyTransferMongoRepository extends MongoRepository<CaptaincyTransferDocument, UUID> {

    Optional<CaptaincyTransferDocument> findByTeamIdAndStatus(UUID teamId, TransferRequestStatus status);
}
