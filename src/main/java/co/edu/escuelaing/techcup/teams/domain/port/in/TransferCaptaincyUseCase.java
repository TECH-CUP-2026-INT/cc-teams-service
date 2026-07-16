package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;

import java.util.UUID;

public interface TransferCaptaincyUseCase {

    CaptaincyTransferRequest initiateTransfer(UUID captainId, UUID teamId, UUID newCaptainId);

    CaptaincyTransferRequest applyForCaptaincy(UUID playerId, String playerName, UUID teamId);

    CaptaincyTransferRequest respondToTransfer(UUID userId, UUID transferId, boolean accept);
}
