package co.edu.escuelaing.techcup.teams.infrastructure.adapter.in.rest.handler;

import co.edu.escuelaing.techcup.teams.domain.exception.DomainException;
import co.edu.escuelaing.techcup.teams.domain.exception.InvalidInvitationStateException;
import co.edu.escuelaing.techcup.teams.domain.exception.InvitationNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.PlayerAlreadyInTeamException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamFullException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNameAlreadyExistsException;
import co.edu.escuelaing.techcup.teams.domain.exception.TeamNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.TransferNotFoundException;
import co.edu.escuelaing.techcup.teams.domain.exception.UnauthorizedTeamActionException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesTeamNotFound() {
        ResponseEntity<Map<String, Object>> response = handler.handleTeamNotFound(new TeamNotFoundException(UUID.randomUUID()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).containsEntry("errorCode", "TEAM_NOT_FOUND");
    }

    @Test
    void handlesInvitationNotFound() {
        ResponseEntity<Map<String, Object>> response = handler.handleInvitationNotFound(new InvitationNotFoundException(UUID.randomUUID()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void handlesTransferNotFound() {
        ResponseEntity<Map<String, Object>> response = handler.handleTransferNotFound(new TransferNotFoundException(UUID.randomUUID()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void handlesTeamNameExists() {
        ResponseEntity<Map<String, Object>> response = handler.handleTeamNameExists(new TeamNameAlreadyExistsException("Halcones"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handlesPlayerAlreadyInTeam() {
        ResponseEntity<Map<String, Object>> response = handler.handlePlayerAlreadyInTeam(new PlayerAlreadyInTeamException(UUID.randomUUID()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handlesUnauthorized() {
        ResponseEntity<Map<String, Object>> response = handler.handleUnauthorized(new UnauthorizedTeamActionException("nope"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void handlesTeamFull() {
        ResponseEntity<Map<String, Object>> response = handler.handleTeamFull(new TeamFullException(UUID.randomUUID()));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void handlesInvalidState() {
        ResponseEntity<Map<String, Object>> response = handler.handleInvalidState(new InvalidInvitationStateException("bad state"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handlesGenericDomainException() {
        ResponseEntity<Map<String, Object>> response = handler.handleDomainException(new DomainException("SOME_ERROR", "message"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handlesAccessDeniedAs403() {
        ResponseEntity<Map<String, Object>> response = handler.handleAccessDenied(new AccessDeniedException("denied"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).containsEntry("errorCode", "ACCESS_DENIED");
    }

    @Test
    void handlesMalformedJson() {
        HttpMessageNotReadableException ex = mock(HttpMessageNotReadableException.class);
        ResponseEntity<Map<String, Object>> response = handler.handleMalformedJson(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("errorCode", "MALFORMED_JSON");
    }

    @Test
    void handlesUnsupportedMediaType() {
        HttpMediaTypeNotSupportedException ex = mock(HttpMediaTypeNotSupportedException.class);
        ResponseEntity<Map<String, Object>> response = handler.handleUnsupportedMediaType(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void handlesMissingHeader() {
        MissingRequestHeaderException ex = mock(MissingRequestHeaderException.class);
        when(ex.getHeaderName()).thenReturn("Authorization");
        ResponseEntity<Map<String, Object>> response = handler.handleMissingHeader(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("message")).isEqualTo("Required header is missing: Authorization");
    }

    @Test
    void handlesTypeMismatch() {
        MethodArgumentTypeMismatchException ex = mock(MethodArgumentTypeMismatchException.class);
        when(ex.getName()).thenReturn("teamId");
        ResponseEntity<Map<String, Object>> response = handler.handleTypeMismatch(ex);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("errorCode")).isEqualTo("INVALID_REQUEST_PARAMETER");
    }

    @Test
    void handlesValidationErrors() {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError("request", "name", "Name is required"));
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationErrors(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().get("errorCode")).isEqualTo("VALIDATION_ERROR");
        @SuppressWarnings("unchecked")
        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
        assertThat(errors).containsEntry("name", "Name is required");
    }

    @Test
    void handlesGenericException() {
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(new RuntimeException("boom"));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("errorCode", "INTERNAL_ERROR");
    }
}
