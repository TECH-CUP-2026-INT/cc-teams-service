package co.edu.escuelaing.techcup.teams.domain.port.out;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;

import java.util.Optional;

public interface CaptaincyTransferRepositoryPort {

    CaptaincyTransferRequest save(CaptaincyTransferRequest request);

    Optional<CaptaincyTransferRequest> findById(String id);

    Optional<CaptaincyTransferRequest> findByTeamIdAndStatus(String teamId, TransferRequestStatus status);
}
