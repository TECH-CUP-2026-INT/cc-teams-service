# Configuration

## Clone the repository

```bash
git clone https://github.com/TECH-CUP-2026-INT/cc-teams-service.git
cd cc-teams-service
```

## Run the service locally

### Option 1: Docker Compose (recommended)

Starts MongoDB and lets you run the service against it:

```bash
docker compose up -d
./mvnw spring-boot:run
```

The service is available at `http://localhost:5622` (the default port set
in `application.yml`). MongoDB is exposed on the host at `27017`.

### Option 2: Local Maven only

Requires Java 21, Maven (or the included wrapper), and a reachable MongoDB
instance (local or a managed provider such as MongoDB Atlas):

```bash
export MONGODB_URI=mongodb://localhost:27017/teams_service
./mvnw spring-boot:run
```

## Run with standalone Docker

```bash
docker build -t cc-teams-service:latest .
docker run --rm -p 5622:5622 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/teams_service \
  -e IDENTITY_SERVICE_URL=http://identity-service:5620 \
  cc-teams-service:latest
```

## Environment variables

| Variable | Default value | Use |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/teams_service` | MongoDB connection string |
| `SERVER_PORT` | `5622` | HTTP port the service listens on |
| `IDENTITY_SERVICE_URL` | `http://identity-service:5620` | Base URL of Identity Service, used to validate JWTs remotely on every authenticated request |
| `COMMUNICATIONS_URL` | `http://localhost:8083` | Base URL for sending invitation/captaincy-transfer notifications |
| `TOURNAMENT_SERVICE_URL` | `http://localhost:8080` | Base URL of `mk-tournament-service` (that repo has no `server.port` override, so it runs on Spring Boot's own default, 8080) |

These variables are resolved in `src/main/resources/application.yml`.

!!! danger "localhost defaults are a known, tracked risk in deployed environments"
    All three service URLs (`IDENTITY_SERVICE_URL`, `COMMUNICATIONS_URL`,
    `TOURNAMENT_SERVICE_URL`) default to `localhost`. In any deployed
    environment they **must** be overridden with the real reachable URL of
    each service — otherwise the service still starts and answers health
    checks, but every cross-service call (JWT validation, notifications,
    tournament enrollment) fails.

## Documentation (MkDocs)

This service's technical documentation is built with
[MkDocs](https://www.mkdocs.org/) and the
[Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) theme.

### Installation

```bash
python -m venv .venv
# Linux / macOS
source .venv/bin/activate
# Windows (PowerShell)
.venv\Scripts\Activate.ps1

pip install mkdocs-material
```

### Serve the documentation locally

From the repository root (where `mkdocs.yml` lives):

```bash
mkdocs serve
```

Starts a local server at
[http://127.0.0.1:8000](http://127.0.0.1:8000) with live reload.

### Build the static site

```bash
mkdocs build
```

Generates the site into `site/` (git-ignored), ready to be published as
static content — see [Deployment](anexos.md#cicd-pipeline) for how this
happens automatically via GitHub Pages.

### Documentation structure

```
project/
│
├── docs/
│   ├── index.md
│   ├── introduccion.md
│   ├── requerimientos.md
│   ├── configuracion.md
│   ├── arquitectura.md
│   ├── api.md
│   ├── pruebas.md
│   ├── equipo.md
│   ├── anexos.md
│   └── assets/
│       ├── img/
│       ├── diagrams/
│       └── stylesheets/
│           └── extra.css
│
├── mkdocs.yml
├── src/
```

Theme colors and typography are defined in
`docs/assets/stylesheets/extra.css` and declared in `mkdocs.yml` under
`extra_css`.
