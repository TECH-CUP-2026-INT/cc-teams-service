package co.edu.escuelaing.techcup.teams.domain.model;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {

    private String id;
    private String teamId;
    private String userId;
    private AuditActionType actionType;
    private String description;
    private boolean success;
    private LocalDateTime timestamp;
}
