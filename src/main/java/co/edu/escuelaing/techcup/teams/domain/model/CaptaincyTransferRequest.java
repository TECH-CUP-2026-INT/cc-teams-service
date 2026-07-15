package co.edu.escuelaing.techcup.teams.domain.model;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptaincyTransferRequest {

    private String id;
    private String teamId;
    private String teamName;
    private String currentCaptainId;
    private String newCaptainId;
    private String initiatedBy;
    private TransferRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
