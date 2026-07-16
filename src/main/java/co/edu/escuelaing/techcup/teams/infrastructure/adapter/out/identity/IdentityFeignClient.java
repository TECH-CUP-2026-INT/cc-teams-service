package co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.identity;

import co.edu.escuelaing.techcup.teams.infrastructure.adapter.out.identity.dto.TokenValidationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "identity-service", url = "${identity.service.base-url}")
public interface IdentityFeignClient {

    @PostMapping("/api/v1/token/validate")
    TokenValidationResponseDTO validar(@RequestHeader("Authorization") String bearerToken);
}
