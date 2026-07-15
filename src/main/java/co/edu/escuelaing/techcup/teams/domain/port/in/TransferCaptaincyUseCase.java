package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;

public interface TransferCaptaincyUseCase {

    CaptaincyTransferRequest initiateTransfer(String captainId, String teamId, String newCaptainId);

    CaptaincyTransferRequest applyForCaptaincy(String playerId, String playerName, String teamId);

    CaptaincyTransferRequest respondToTransfer(String userId, String transferId, boolean accept);
}
