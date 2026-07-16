package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.controller;

import co.edu.escuelaing.techcup.teams.domain.enums.TransferRequestStatus;
import co.edu.escuelaing.techcup.teams.domain.model.CaptaincyTransferRequest;
import co.edu.escuelaing.techcup.teams.domain.port.in.TransferCaptaincyUseCase;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.dto.response.CaptaincyTransferResponse;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.handler.GlobalExceptionHandler;
import co.edu.escuelaing.techcup.teams.infrastructure.mapper.CaptaincyTransferMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = CaptaincyController.class,
        excludeAutoConfiguration = {
                SecurityAutoConfiguration.class,
                SecurityFilterAutoConfiguration.class
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class CaptaincyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransferCaptaincyUseCase transferCaptaincyUseCase;
    @MockBean
    private CaptaincyTransferMapper transferMapper;

    private final UUID captainId = UUID.randomUUID();
    private final UUID teamId = UUID.randomUUID();
    private final UUID newCaptainId = UUID.randomUUID();
    private final UUID transferId = UUID.randomUUID();

    private Principal principalFor(UUID userId) {
        return new UsernamePasswordAuthenticationToken(userId, "token", List.of());
    }

    private CaptaincyTransferRequest transfer() {
        return CaptaincyTransferRequest.builder()
                .id(transferId).teamId(teamId).teamName("Los Halcones FC")
                .currentCaptainId(captainId).newCaptainId(newCaptainId)
                .initiatedBy("CAPTAIN").status(TransferRequestStatus.PENDING).build();
    }

    private CaptaincyTransferResponse transferResponse() {
        return CaptaincyTransferResponse.builder()
                .id(transferId).teamId(teamId).teamName("Los Halcones FC")
                .currentCaptainId(captainId).newCaptainId(newCaptainId)
                .initiatedBy("CAPTAIN").status(TransferRequestStatus.PENDING).build();
    }

    @Test
    void initiateTransferReturns201() throws Exception {
        when(transferCaptaincyUseCase.initiateTransfer(eq(captainId), eq(teamId), eq(newCaptainId)))
                .thenReturn(transfer());
        when(transferMapper.toResponse(any())).thenReturn(transferResponse());

        mockMvc.perform(post("/api/v1/captaincy/teams/" + teamId + "/transfer")
                        .principal(principalFor(captainId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newCaptainId\":\"" + newCaptainId + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void applyForCaptaincyReturns201() throws Exception {
        when(transferCaptaincyUseCase.applyForCaptaincy(eq(newCaptainId), any(), eq(teamId)))
                .thenReturn(transfer());
        when(transferMapper.toResponse(any())).thenReturn(transferResponse());

        mockMvc.perform(post("/api/v1/captaincy/teams/" + teamId + "/apply")
                        .principal(principalFor(newCaptainId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerName\":\"New Captain\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void respondToTransferReturns200() throws Exception {
        when(transferCaptaincyUseCase.respondToTransfer(eq(newCaptainId), eq(transferId), eq(true)))
                .thenReturn(transfer());
        when(transferMapper.toResponse(any())).thenReturn(transferResponse());

        mockMvc.perform(put("/api/v1/captaincy/" + transferId + "/respond")
                        .principal(principalFor(newCaptainId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accept\":true}"))
                .andExpect(status().isOk());
    }

    @Test
    void initiateTransferRejectsMalformedTeamIdInPath() throws Exception {
        mockMvc.perform(post("/api/v1/captaincy/teams/not-a-uuid/transfer")
                        .principal(principalFor(captainId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newCaptainId\":\"" + newCaptainId + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("INVALID_REQUEST_PARAMETER"));
    }
}
