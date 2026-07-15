package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.port.in.AuditQueryUseCase;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditQueryUseCaseImpl implements AuditQueryUseCase {

    private final AuditEventRepositoryPort auditRepository;

    @Override
    public List<AuditEvent> queryEvents(LocalDateTime startDate, LocalDateTime endDate,
                                        AuditActionType actionType, String teamId) {
        return auditRepository.findByFilters(startDate, endDate, actionType, teamId);
    }
}
