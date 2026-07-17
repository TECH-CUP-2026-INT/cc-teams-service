# Proposal: Deploy cc-teams-service to Azure (deploy-teams-azure)

## Proposal question round

No product question round was run — this is a pure infra/deploy change. Assumptions (user-confirmed via orchestrator):
- Scope = config + deploy only. No source code changes.
- `IDENTITY_SERVICE_URL`, `COMMUNICATIONS_URL`, `TOURNAMENT_SERVICE_URL` stay at `localhost` defaults (documented risk: cross-service calls fail in Azure, service still starts).
- `ci.yml` and `Dockerfile` are already prepared; this change pushes them to `main` to trigger the existing pipeline.

## Intent

Make `cc-teams-service` (artifactId `service-teams`) production-available by deploying it to Azure App Service `teams-service` (Web App for Containers, region `brazilsouth-01`) using the existing `ci.yml` pipeline that builds, dockerizes to GHCR, and deploys via `azure/webapps-deploy@v3`.

## Scope

### In Scope
- Create GitHub repo secrets: `AZURE_WEBAPP_NAME`, `AZURE_WEBAPP_PUBLISH_PROFILE`.
- Set Azure App Service app settings: `WEBSITES_PORT=8082`, `MONGODB_URI`, `IDENTITY_SERVICE_URL`, `COMMUNICATIONS_URL`, `TOURNAMENT_SERVICE_URL`.
- Push already-prepared `.github/workflows/ci.yml` + `Dockerfile` to `main` to trigger the pipeline.
- Verify `deploy` job is green and health endpoint responds.

### Out of Scope
- No source code changes.
- No deploy of other microservices.
- No cross-service URL wiring for Azure (deferred).
- No runbook/deployment doc (deferred).

## Capabilities

### New Capabilities
- None

### Modified Capabilities
- None

> Pure config/deploy change. No spec-level behavior changes — no delta or new specs required. The `sdd-spec` phase will be a no-op at the capability level.

## Approach

Leverage the existing CI pipeline as-is. Provision secrets + App Service env vars, then push the prepared `ci.yml`/`Dockerfile` to `main`. The pipeline builds, runs tests against `mongo:7`, dockerizes to `ghcr.io/TECH-CUP-2026-INT/cc-teams-service:latest`, and deploys the container to the Azure Web App. No code or image changes.

## Affected Areas

| Area | Impact | Description |
|------|--------|-------------|
| `.github/workflows/ci.yml` | Push (prepared) | Already prepared; triggers deploy on `main`. |
| `Dockerfile` | Push (prepared) | Multi-stage, `EXPOSE 8082`; already prepared. |
| GitHub repo secrets | New | `AZURE_WEBAPP_NAME`, `AZURE_WEBAPP_PUBLISH_PROFILE`. |
| Azure App Service config | New | `WEBSITES_PORT`, `MONGODB_URI`, service URLs app settings. |

## Risks

| Risk | Likelihood | Mitigation |
|------|------------|------------|
| Secret missing before push | High | Create both secrets before pushing to `main`. |
| `WEBSITES_PORT` missing | Med | Set `WEBSITES_PORT=8082` explicitly. |
| `MONGODB_URI` missing | Med | Set app setting with CosmosDB/Mongo connection string. |
| localhost service URLs | High | Documented; cross-service calls fail, service starts (deferred). |
| Publish profile leak (in chat) | Med | Rotate publish profile post-deploy; see rollback. |

## Rollback Plan

- Stop/disable the App Service deployment (stop site) or redeploy the previous GHCR image tag.
- Delete the GitHub repo secrets if aborting entirely.
- Rotate the Azure publish profile (already exposed in chat) after any deploy or rollback.

## Dependencies

- Existing `ci.yml` + `Dockerfile` (prepared, pending push).
- Azure App Service `teams-service` (URL `https://teams-service-fhc4dxdnffgyewct.brazilsouth-01.azurewebsites.net`).
- CosmosDB/Mongo `MONGODB_URI` (obtained).

## Success Criteria

- [ ] GitHub Actions `deploy` job green on `main`.
- [ ] App Service responds at `https://teams-service-fhc4dxdnffgyewct.brazilsouth-01.azurewebsites.net`.
- [ ] Container logs show successful Mongo connection.
