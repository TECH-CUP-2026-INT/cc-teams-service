package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.persistence.document;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
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
@Document(collection = "audit_events")
public class AuditEventDocument {

    @Id
    private String id;

    @Indexed
    private String teamId;
    private String userId;
    private AuditActionType actionType;
    private String description;
    private boolean success;

    @Indexed
    private LocalDateTime timestamp;
}
