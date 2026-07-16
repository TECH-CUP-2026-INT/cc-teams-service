package co.edu.escuelaing.techcup.teams.domain.port.out;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;

import java.util.Optional;
import java.util.UUID;

public interface CaptaincyTransferRepositoryPort {

    CaptaincyTransferRequest save(CaptaincyTransferRequest request);

    Optional<CaptaincyTransferRequest> findById(UUID id);

    Optional<CaptaincyTransferRequest> findByTeamIdAndStatus(UUID teamId, TransferRequestStatus status);
}
