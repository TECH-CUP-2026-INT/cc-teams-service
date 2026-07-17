# Deployment Readiness Specification

## Purpose

This spec defines the REQUIREMENTS a deploy of `cc-teams-service` to Azure App Service `teams-service` MUST satisfy to be considered correct. It covers pre-deploy configuration, CI pipeline behavior, post-deploy verification, and rollback — NOT source-code behavior (no capability change).

## Requirements

### Requirement: GitHub Repo Secrets MUST Exist Before Deploy

The repository MUST have secrets `AZURE_WEBAPP_NAME` and `AZURE_WEBAPP_PUBLISH_PROFILE` configured before any `main`-triggered deploy job executes.

#### Scenario: Secrets present before push to main

- GIVEN the repository has both `AZURE_WEBAPP_NAME` and `AZURE_WEBAPP_PUBLISH_PROFILE` secrets defined
- WHEN a push to `main` triggers the `deploy` job
- THEN the job MUST start the Azure webapp deploy step without a missing-secret failure

#### Scenario: Missing secret blocks deploy

- GIVEN at least one of the two required secrets is undefined
- WHEN the `deploy` job runs
- THEN the deploy step MUST fail and report the missing secret

### Requirement: App Service Port Configuration MUST Match Container

Azure App Service `teams-service` MUST have the application setting `WEBSITES_PORT=5622` set, matching the Dockerfile `EXPOSE 5622`.

#### Scenario: Port configured correctly

- GIVEN the App Service application settings include `WEBSITES_PORT` = `5622`
- WHEN the container starts
- THEN the App Service MUST route ingress to container port 5622

#### Scenario: Port mismatch

- GIVEN `WEBSITES_PORT` is unset or not `5622`
- WHEN the container starts
- THEN the App Service MUST NOT route traffic to the running service (connection failure)

### Requirement: App Service MUST Have Reachable MongoDB URI

Azure App Service `teams-service` MUST have the application setting `MONGODB_URI` set to a reachable MongoDB endpoint.

#### Scenario: Valid Mongo URI

- GIVEN `MONGODB_URI` points to a reachable MongoDB/CosmosDB endpoint
- WHEN the container starts
- THEN the application MUST establish a Mongo connection on startup

#### Scenario: Unreachable Mongo URI

- GIVEN `MONGODB_URI` is unset or unreachable
- WHEN the container starts
- THEN the application MUST fail to connect and the container logs MUST show the Mongo connection error

### Requirement: CI Pipeline MUST Build, Push, and Deploy on Main

On a push to `main`, `ci.yml` MUST: (1) build and test the application, (2) build and push the Docker image to `ghcr.io/TECH-CUP-2026-INT/cc-teams-service:latest`, and (3) deploy the container to the App Service using the publish profile.

#### Scenario: Full pipeline success

- GIVEN a push to `main` with valid secrets and a passing test suite
- WHEN the `ci.yml` workflow runs
- THEN it MUST build & test, push the image to the GHCR `:latest` tag, and deploy to App Service — in that order

#### Scenario: Test failure halts deploy

- GIVEN the build-and-test job fails
- WHEN `ci.yml` runs
- THEN the pipeline MUST NOT push the image or run the deploy job

### Requirement: Post-Deploy Health MUST Be Verified

After deploy, the App Service health endpoint (e.g. `/actuator/health` if present, otherwise root) SHOULD respond with HTTP 200, and container logs SHOULD show a successful Mongo connection.

#### Scenario: Healthy deploy

- GIVEN a completed deploy with valid `MONGODB_URI`
- WHEN the health endpoint is requested
- THEN it SHOULD return 200 AND logs SHOULD show successful Mongo connection

### Requirement: Rollback MUST Be Possible

If deploy fails, the previous GHCR image tag MUST be redeployable; GitHub repo secrets MAY be removed on abort.

#### Scenario: Redeploy previous image

- GIVEN a failed or bad deploy
- WHEN an operator redeploys the previous GHCR image tag to App Service
- THEN the prior working container MUST become the running instance

#### Scenario: Abort removes secrets

- GIVEN the deploy is aborted entirely
- WHEN an operator chooses to clean up
- THEN the GitHub repo secrets MAY be deleted
