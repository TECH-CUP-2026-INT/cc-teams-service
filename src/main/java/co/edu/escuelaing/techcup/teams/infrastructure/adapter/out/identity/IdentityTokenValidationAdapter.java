package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.identity;

import co.edu.escuelaing.techcup.teams.domain.port.out.IdentityTokenValidationPort;
import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.identity.dto.TokenValidationResponseDTO;
import feign.FeignException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IdentityTokenValidationAdapter implements IdentityTokenValidationPort {

    private final IdentityFeignClient identityFeignClient;

    public IdentityTokenValidationAdapter(IdentityFeignClient identityFeignClient) {
        this.identityFeignClient = identityFeignClient;
    }

    @Override
    public Optional<TokenInfo> validar(String bearerToken) {
        try {
            TokenValidationResponseDTO response = identityFeignClient.validar(bearerToken);

            if (response == null || !response.valid()) {
                return Optional.empty();
            }
            return Optional.of(new TokenInfo(response.userId(), response.email(), response.role()));
        } catch (FeignException e) {
            return Optional.empty();
        }
    }
}
