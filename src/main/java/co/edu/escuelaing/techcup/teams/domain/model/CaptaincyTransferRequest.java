package co.edu.escuelaing.techcup.teams.domain.model;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptaincyTransferRequest {

    private UUID id;
    private UUID teamId;
    private String teamName;
    private UUID currentCaptainId;
    private UUID newCaptainId;
    private String initiatedBy;
    private TransferRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
