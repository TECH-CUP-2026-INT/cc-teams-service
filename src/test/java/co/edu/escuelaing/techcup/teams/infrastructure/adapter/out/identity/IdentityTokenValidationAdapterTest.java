package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.identity;

import co.edu.escuelaing.techcup.teams.domain.port.out.IdentityTokenValidationPort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.identity.dto.TokenValidationResponseDTO;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentityTokenValidationAdapterTest {

    @Mock
    private IdentityFeignClient identityFeignClient;

    private IdentityTokenValidationAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new IdentityTokenValidationAdapter(identityFeignClient);
    }

    @Test
    void retornaTokenInfoCuandoElTokenEsValido() {
        when(identityFeignClient.validar("Bearer abc123"))
                .thenReturn(new TokenValidationResponseDTO(true, "11111111-1111-1111-1111-111111111111",
                        "captain@techcup.com", "CAPTAIN"));

        Optional<IdentityTokenValidationPort.TokenInfo> resultado = adapter.validar("Bearer abc123");

        assertThat(resultado).isPresent();
        assertThat(resultado.get().role()).isEqualTo("CAPTAIN");
    }

    @Test
    void retornaVacioCuandoIdentityRespondeInvalido() {
        when(identityFeignClient.validar("Bearer expirado"))
                .thenReturn(new TokenValidationResponseDTO(false, null, null, null));

        assertThat(adapter.validar("Bearer expirado")).isEmpty();
    }

    @Test
    void retornaVacioCuandoIdentityRespondeError() {
        when(identityFeignClient.validar("Bearer invalido")).thenThrow(mock(FeignException.class));

        assertThat(adapter.validar("Bearer invalido")).isEmpty();
    }
}
