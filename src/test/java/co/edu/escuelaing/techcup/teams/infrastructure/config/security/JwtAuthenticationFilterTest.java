package co.edu.escuelaing.techcup.teams.infrastructure.config.security;

import co.edu.escuelaing.techcup.teams.domain.port.out.IdentityTokenValidationPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private IdentityTokenValidationPort identityTokenValidationPort;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(identityTokenValidationPort);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doesNotAuthenticateWhenNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doesNotAuthenticateWhenHeaderHasNoBearerPrefix() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doesNotAuthenticateWhenIdentityRejectsTheToken() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalido");
        when(identityTokenValidationPort.validar("Bearer invalido")).thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doesNotAuthenticateWhenIdentityReturnsANonUuidUserId() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer valido");
        when(identityTokenValidationPort.validar("Bearer valido"))
                .thenReturn(Optional.of(new IdentityTokenValidationPort.TokenInfo("not-a-uuid", "a@b.com", "PLAYER")));

        filter.doFilter(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void authenticatesWithUuidPrincipalAndRoleAuthorityForValidToken() throws Exception {
        UUID userId = UUID.randomUUID();
        when(request.getHeader("Authorization")).thenReturn("Bearer valido-captain");
        when(identityTokenValidationPort.validar("Bearer valido-captain"))
                .thenReturn(Optional.of(new IdentityTokenValidationPort.TokenInfo(userId.toString(), "captain@techcup.com", "CAPTAIN")));

        filter.doFilter(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isEqualTo(userId);
        assertThat(authentication.getCredentials()).isEqualTo("Bearer valido-captain");
        assertThat(authentication.getAuthorities())
                .extracting(Object::toString)
                .containsExactly("ROLE_CAPTAIN");
        verify(filterChain).doFilter(request, response);
    }
}
