package co.edu.escuelaing.techcup.teams.application.usecase;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.port.out.AuditEventRepositoryPort;
import co.edu.escuelaing.techcup.teams.support.TestFixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditQueryUseCaseImplTest {

    @Mock
    private AuditEventRepositoryPort auditRepository;

    @InjectMocks
    private AuditQueryUseCaseImpl useCase;

    @Test
    void queryEventsDelegatesFiltersToRepository() {
        LocalDateTime start = LocalDateTime.of(2026, Month.JANUARY, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, Month.JANUARY, 31, 23, 59);
        List<AuditEvent> expected = List.of(TestFixtures.auditEvent());
        when(auditRepository.findByFilters(start, end, AuditActionType.TEAM_CREATED, TestFixtures.TEAM_ID))
                .thenReturn(expected);

        List<AuditEvent> result = useCase.queryEvents(start, end, AuditActionType.TEAM_CREATED, TestFixtures.TEAM_ID);

        assertThat(result).isSameAs(expected);
        verify(auditRepository).findByFilters(start, end, AuditActionType.TEAM_CREATED, TestFixtures.TEAM_ID);
    }
}
