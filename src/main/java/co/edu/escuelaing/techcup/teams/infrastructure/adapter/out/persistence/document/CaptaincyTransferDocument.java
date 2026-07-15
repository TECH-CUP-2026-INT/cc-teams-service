package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "captaincy_transfers")
public class CaptaincyTransferDocument {

    @Id
    private String id;

    @Indexed
    private String teamId;
    private String teamName;
    private String currentCaptainId;
    private String newCaptainId;
    private String initiatedBy;
    private TransferRequestStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
}
