package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.enums.AuditActionType;
import co.edu.escuelaing.techcup.teams.domain.model.AuditEvent;
import co.edu.escuelaing.techcup.teams.domain.port.in.AuditQueryUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.AuditEventResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.handler.GlobalExceptionHandler;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.AuditEventMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = AuditController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuditControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditQueryUseCase auditQueryUseCase;
    @MockBean
    private AuditEventMapper auditEventMapper;

    @Test
    void queryEventsReturnsList() throws Exception {
        UUID teamId = UUID.randomUUID();
        AuditEvent event = AuditEvent.builder().id(UUID.randomUUID()).teamId(teamId)
                .actionType(AuditActionType.TEAM_CREATED).success(true).timestamp(LocalDateTime.now()).build();
        when(auditQueryUseCase.queryEvents(any(), any(), any(), any())).thenReturn(List.of(event));
        when(auditEventMapper.toResponse(any())).thenReturn(
                AuditEventResponse.builder().id(event.getId()).teamId(teamId)
                        .actionType(AuditActionType.TEAM_CREATED).success(true).build());

        mockMvc.perform(get("/api/v1/audit").param("teamId", teamId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].teamId").value(teamId.toString()));
    }

    @Test
    void rejectsInvalidDateRange() throws Exception {
        mockMvc.perform(get("/api/v1/audit")
                        .param("startDate", "2026-01-31T00:00:00")
                        .param("endDate", "2026-01-01T00:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsMalformedTeamIdParam() throws Exception {
        mockMvc.perform(get("/api/v1/audit").param("teamId", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_PARAMETER"));
    }
}
