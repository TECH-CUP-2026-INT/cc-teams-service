# Appendices

## Glossary

| Term | Meaning |
|---|---|
| Captain | Team member with `TeamMemberRole.CAPTAIN`; the only one who can send invitations, initiate a captaincy transfer, or enroll the team in a tournament |
| Roster | The list of a team's members |
| Enrollment | A team's registration in a specific tournament, managed by `mk-tournament-service` |
| Port (architecture) | A domain interface that abstracts an external integration |
| Fails open | A check that defaults to a permissive result (e.g. `false` / "no active tournament") when the service it depends on is unreachable, rather than blocking the caller |
| `ROLE_<role>` | Spring Security authority derived from the role Identity Service returns for a validated JWT |

## Security notes

Current security posture of this service, based on the implementation:

- **JWT trust is fully delegated to Identity Service.** This service has no
  local signing key and performs no signature verification — every
  authenticated request triggers a remote call to Identity's
  `POST /api/v1/token/validate`. This is a deliberate design choice, but it
  means Teams Service's own availability for protected endpoints is capped
  by Identity Service's availability.
- **CSRF protection is disabled** (`.csrf(AbstractHttpConfigurer::disable)`),
  consistent with a stateless, token-based API that never issues a browser
  session cookie.
- **CORS currently only allows `http://localhost:3000` and
  `http://localhost:4200`** as origins. This needs to be updated with the
  real frontend domain(s) before any non-local environment can be called
  from a browser.
- **Three service-to-service endpoints carry no application-level
  authentication** (`GET /teams/{teamId}`, `.../by-player/{playerId}/roster`,
  `.../by-player/{playerId}/active-tournament`). They're explicitly
  `permitAll`'d in `SecurityConfig` and rely entirely on network-level
  isolation — reachable only from other services inside the trusted
  network, never from the public internet.
- **Invitation and captaincy notifications are not actually sent yet.**
  `NotificationAdapter` (the implementation of `NotificationPort`) is a
  stub that only logs the event — see `infrastructure/adapter/out/notification/NotificationAdapter.java`.
  It's explicitly excluded from the JaCoCo coverage requirement for the
  same reason. Replacing it with a real call to the Communications service
  is an open item, not a regression.

**Operational implication:** because the three service-to-service endpoints
above trust the network rather than a credential, and because JWT trust is
delegated rather than locally verified, this service should never be
exposed directly to the public internet — only to the other trusted
services and to whatever gateway/proxy fronts it in production.

## CI/CD pipeline

Defined in `.github/workflows/ci.yml`. Three jobs, each gated on the
previous one succeeding:

1. **`build-test`** — `mvn clean verify -B` against an ephemeral MongoDB
   service container. Runs on every push to `feat/**`, `develop`, `main`,
   and every PR into `develop`/`main`.
2. **`dockerize-publish`** — builds the JAR and pushes the image to
   `ghcr.io/TECH-CUP-2026-INT/cc-teams-service`, tagged by branch, short
   SHA, and (only on `main`) `latest`.
3. **`deploy`** — only on `main`. Uses `azure/webapps-deploy@v3` with a
   publish-profile secret to redeploy the Azure App Service `teams-service`
   (region `brazilsouth-01`) with the freshly pushed image.

Required GitHub secrets: `AZURE_WEBAPP_NAME`, `AZURE_WEBAPP_PUBLISH_PROFILE`
(an app-level credential, chosen specifically because it doesn't require
Microsoft Entra ID / App Registration access).

Required Azure App Service application settings: `WEBSITES_PORT=5622`,
`MONGODB_URI`, `IDENTITY_SERVICE_URL`, `COMMUNICATIONS_URL`,
`TOURNAMENT_SERVICE_URL` — see [Configuration](configuracion.md) for what
each one does and the risk of leaving them at their `localhost` defaults.

A separate workflow, `.github/workflows/deploy-mkdocs.yml`, builds this
documentation site and publishes it to GitHub Pages on every push to
`main`. It requires this repository's **Settings → Pages → Build and
deployment → Source** to be set to **"GitHub Actions"** — a one-time manual
toggle, otherwise the workflow runs but nothing gets published.

## References

- [MkDocs](https://www.mkdocs.org/) — static documentation site generator
  used by this project.
- [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) —
  theme used for the site.
- [Spring Boot](https://spring.io/projects/spring-boot) — the service's
  framework.
- [springdoc-openapi](https://springdoc.org/) — OpenAPI specification and
  Swagger UI generation.
- [JaCoCo](https://www.jacoco.org/jacoco/) — test coverage.
- [OpenFeign](https://spring.io/projects/spring-cloud-openfeign) — declarative
  HTTP clients used for the Identity and Tournament integrations.

## Changelog

| Date | Change |
|---|---|
| 2026-07-17 | Documentation restructured to match the `am-logistic-service` documentation pattern (Home / Introduction / Requirements / Configuration / Architecture / API / Testing / Team / Appendices), with content verified against the actual controllers, use cases, and adapters rather than the earlier generic stub pages. |
