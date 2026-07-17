# Testing

## How to run the tests

```bash
# Full suite
./mvnw test

# Full suite + coverage gate (JaCoCo)
./mvnw verify
```

## What the tests cover

| Area | Covers |
|---|---|
| `application/usecase/*Test` | Business logic for team creation, invitations, captaincy transfer, and tournament enrollment, including duplicate/not-found/unauthorized edge cases |
| `infrastructure/adapter/in/rest/controller/*Test` | HTTP contract of each endpoint: `201`/`200` on the happy path, `401`/`403` on auth failures, `400` on payload validation |
| `infrastructure/config/security/*` | `JwtAuthenticationFilter` behavior when Identity Service approves, rejects, or is unreachable |
| `infrastructure/adapter/out/*` | Feign adapters toward Identity and Tournament Service: happy path and failure handling (`FeignException` mapped to the right domain exception) |
| `infrastructure/adapter/in/rest/handler/GlobalExceptionHandlerTest` | Status codes and response shape for each domain exception |

## Minimum coverage

The `pom.xml` includes `jacoco-maven-plugin` with a minimum line-coverage
rule enforced in the `verify` phase, excluding the application entry point,
generated mapper implementations, DTOs, MongoDB documents, repositories,
and the stub `NotificationAdapter` (packages without meaningful business
logic of their own). If coverage falls below the threshold, the build
fails.

## Tests in the CI pipeline

`.github/workflows/ci.yml` runs on every push to `feat/**`, `develop`, and
`main`, and on every PR into `develop`/`main`:

1. Starts MongoDB as a service container.
2. Sets up JDK 21 with Maven cache.
3. Runs `mvn clean verify -B` — tests plus the JaCoCo coverage gate.
4. Uploads Surefire reports as an artifact on failure.

Only if that job passes does the pipeline move on to building and pushing
the Docker image, and — on `main` — deploying to Azure. See
[Appendices](anexos.md#cicd-pipeline) for the full pipeline breakdown.
