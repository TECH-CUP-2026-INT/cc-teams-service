# Tasks: Deploy cc-teams-service to Azure (deploy-teams-azure)

## Review Workload Forecast

| Field | Value |
|-------|-------|
| Estimated changed lines | ~5-15 (only prepared ci.yml + Dockerfile committed; no new logic) |
| 400-line budget risk | Low |
| Chained PRs recommended | No |
| Suggested split | Single PR |
| Delivery strategy | ask-on-risk |
| Chain strategy | pending |

Decision needed before apply: No
Chained PRs recommended: No
Chain strategy: pending
400-line budget risk: Low

### Suggested Work Units

| Unit | Goal | Likely PR | Focused test command | Runtime harness | Rollback boundary |
|------|------|-----------|----------------------|-----------------|-------------------|
| 1 | configure + push + verify | Single PR | `N/A` — no source code; verify via pipeline + curl | `curl https://teams-service-fhc4dxdnffgyewct.brazilsouth-01.azurewebsites.net` → 200 | revert `ci.yml`/`Dockerfile` commit on `main`; stop App Service |

## Phase 1: Repository Secrets (GitHub)

- [ ] 1.1 Create GitHub repo secret `AZURE_WEBAPP_NAME` = `teams-service` (via `gh secret set` or API).
- [ ] 1.2 Create GitHub repo secret `AZURE_WEBAPP_PUBLISH_PROFILE` = publish profile XML (from Azure portal).

## Phase 2: Azure App Service Config

- [ ] 2.1 In App Service `teams-service` → Configuración → Variables de entorno, set `WEBSITES_PORT`=8082.
- [ ] 2.2 Set `MONGODB_URI` = Cosmos Mongo connection string (`mongodb+srv://rojasriverocarlosduban_db_user:KALI25@team-service.thmvfqe.mongodb.net/?appName=team-service`).
- [ ] 2.3 Set `IDENTITY_SERVICE_URL`, `COMMUNICATIONS_URL`, `TOURNAMENT_SERVICE_URL` = localhost defaults (deferred per user).

## Phase 3: Commit & Trigger Pipeline

- [ ] 3.1 `git add .github/workflows/ci.yml Dockerfile` + commit (msg: `ci: add Azure deploy workflow and Dockerfile for teams-service`).
- [ ] 3.2 `git push origin main` to trigger `ci.yml` deploy job.

## Phase 4: Verification

- [ ] 4.1 Confirm GitHub Actions `deploy` job green on `main`.
- [ ] 4.2 curl `https://teams-service-fhc4dxdnffgyewct.brazilsouth-01.azurewebsites.net` → expect 200 / healthy.
- [ ] 4.3 Check App Service container logs show successful Mongo connection.
- [ ] 4.4 (Rollback test) confirm previous GHCR image tag redeployable; note publish-profile rotation as post-deploy action.

No TDD tasks (no source code). No threat-matrix RED tasks (design marked N/A).
