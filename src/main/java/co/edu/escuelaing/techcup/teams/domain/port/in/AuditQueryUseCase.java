package co.edu.escuelaing.techcup.teams.domain.port.in;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditQueryUseCase {

    List<AuditEvent> queryEvents(LocalDateTime startDate, LocalDateTime endDate,
                                 AuditActionType actionType, UUID teamId);
}
