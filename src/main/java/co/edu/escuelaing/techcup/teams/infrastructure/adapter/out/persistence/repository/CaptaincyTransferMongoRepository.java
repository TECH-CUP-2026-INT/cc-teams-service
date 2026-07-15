package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.repository;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document.CaptaincyTransferDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CaptaincyTransferMongoRepository extends MongoRepository<CaptaincyTransferDocument, String> {

    Optional<CaptaincyTransferDocument> findByTeamIdAndStatus(String teamId, TransferRequestStatus status);
}
