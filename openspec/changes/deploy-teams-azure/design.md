# Design: Deploy cc-teams-service to Azure (deploy-teams-azure)

## Technical Approach

This is a **pure config + deploy change with zero source-code modification**. The technical strategy reuses the EXISTING `.github/workflows/ci.yml` deploy pipeline as-is — it already builds, runs tests against `mongo:7`, dockerizes to `ghcr.io/TECH-CUP-2026-INT/cc-teams-service:latest`, and deploys to Azure via `azure/webapps-deploy@v3` on a `main` push. The operational work is: (1) create 2 GitHub repo secrets, (2) set 4+ Azure App Service application settings, (3) push the already-prepared `ci.yml` + `Dockerfile` to `main` to trigger the pipeline. This maps directly to the proposal's "Approach" and satisfies every requirement in `specs/deployment-readiness/spec.md` (secrets-exist, port-match, mongo-uri, pipeline-order, post-deploy-health, rollback).

## Architecture Decisions

| Decision | Option | Tradeoff | Decision |
|----------|--------|----------|----------|
| Pipeline reuse | Reuse existing `ci.yml` deploy job | +No duplication, identity-service already uses this pattern; −cannot customize deploy step independently | **Reuse** existing job — not a new workflow |
| Secret storage | GitHub repo secrets (`AZURE_WEBAPP_NAME`, `AZURE_WEBAPP_PUBLISH_PROFILE`) + Azure App Service env vars | +Matches identity-service, no Service Principal needed; −publish profile is a long-lived credential that was leaked in chat | **GitHub secrets + App Service settings** (not ACR/SP) |
| Service URLs | Keep `localhost` defaults (`IDENTITY_SERVICE_URL`, `COMMUNICATIONS_URL`, `TOURNAMENT_SERVICE_URL`) | +Service starts; −cross-service Feign calls fail in Azure (deferred, user-confirmed) | **Keep localhost**; real Azure URLs deferred |

## Data Flow

    git push main
         │
         ▼
    GH Actions: build-test (mvn verify, mongo:7)
         │ needs
         ▼
    dockerize-publish (GHCR :latest)
         │ needs
         ▼
    deploy: azure/webapps-deploy@v3 (+ publish profile)
         │
         ▼
    Azure Web App for Containers pulls ghcr image
         │
         ▼
    container runs: WEBSITES_PORT=5622  +  MONGODB_URI
         │
         ▼
    /actuator/health → 200 ; logs show Mongo connect OK

## File Changes

| File | Action | Description |
|------|--------|-------------|
| `.github/workflows/ci.yml` | Push (already prepared) | Triggers `deploy` on `main` via existing job. |
| `Dockerfile` | Push (already prepared) | Multi-stage, `EXPOSE 5622`. |
| GitHub repo secrets | Create | `AZURE_WEBAPP_NAME`, `AZURE_WEBAPP_PUBLISH_PROFILE`. |
| Azure App Service settings | Create | `WEBSITES_PORT=5622`, `MONGODB_URI`, 3 service URLs. |

NO source-code files are touched.

## Interfaces / Contracts

No new interfaces. `application.yml` already binds `${MONGODB_URI}`, `${SERVER_PORT:5622}`, `${IDENTITY_SERVICE_URL}`, `${COMMUNICATIONS_URL}`, `${TOURNAMENT_SERVICE_URL}` — all satisfied by App Service settings. No code contract change.

## Testing Strategy

| Layer | What to Test | Approach |
|-------|-------------|----------|
| Unit | N/A | No code changed. |
| Integration | N/A | No code changed. |
| E2E | Post-deploy readiness (spec requirement) | Request App Service health endpoint → expect HTTP 200; inspect container logs for successful Mongo connection. Pipeline `deploy` job green on `main`. |

## Threat Matrix

N/A — no routing, shell, subprocess, VCS/PR automation, executable-file classification, or NEW process-integration boundary. The only process integration (`azure/webapps-deploy@v3`) already exists in `ci.yml` and is not introduced by this change.

## Migration / Rollout

No data migration. Rollout = single push of prepared `ci.yml`/`Dockerfile` to `main`. Rollback = redeploy previous GHCR tag to App Service, or stop the site; remove repo secrets if aborting entirely.

## Open Questions

- [ ] **Rotate the Azure publish profile** — it was exposed in chat; rotate it after any deploy/rollback (non-blocking per proposal).
