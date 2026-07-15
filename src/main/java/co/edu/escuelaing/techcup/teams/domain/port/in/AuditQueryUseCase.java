package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditQueryUseCase {

    List<AuditEvent> queryEvents(LocalDateTime startDate, LocalDateTime endDate,
                                 AuditActionType actionType, String teamId);
}
