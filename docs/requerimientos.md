# Requirements

Functional requirements taken from the platform's test-case map (TC-20
through TC-28, Teams Service section) contrasted against the current
implementation.

## Functional requirements

| ID | Requirement | Status |
|---|---|---|
| TC-20 | Create team | ✅ Implemented (`POST /api/v1/teams`, multipart with logo) |
| TC-21 | Consult a tournament's registered teams | ✅ Implemented (`GET /api/v1/tournaments/{tournamentId}/teams`) — proxies to `mk-tournament-service`; fails with `502` if that service is unreachable |
| TC-22 | View team detail (with member profiles) | ⚠️ Partially implemented — only unauthenticated, service-to-service endpoints exist (`GET /teams/{teamId}` for name/roster size, `GET /teams/by-player/{playerId}/roster` for member IDs). There is no captain-facing endpoint that enriches a team with full member profiles from `users-players-service` |
| TC-23 | Edit team (name, logo, colors) | ❌ Not implemented — no `PUT`/`PATCH` endpoint exists for an existing team |
| TC-24 | Enroll team in a tournament | ✅ Implemented (`POST /api/v1/tournaments/{tournamentId}/teams/{teamId}/enrollment`) — enforces a minimum of 7 members locally before calling Tournament Service |
| TC-25 | Invite player to team | ✅ Implemented (`POST /api/v1/invitations/teams/{teamId}`) — does **not** verify the invited player's existence against `users-players-service`; the given `UUID` is trusted as-is |
| TC-26 | Accept/reject invitation | ✅ Implemented (`PUT /api/v1/invitations/{invitationId}/respond`) |
| TC-27 | Transfer captaincy | ✅ Implemented (`POST /api/v1/captaincy/teams/{teamId}/transfer`, `.../apply`, `PUT /api/v1/captaincy/{transferId}/respond`) — updates the captain locally only; does not push a role update to Identity Service, which is intentional and consistent with Identity's current design (it queries `users-players-service` live for role instead of accepting pushed updates) |
| TC-28 | Delete team | ❌ Not implemented — no `DELETE` endpoint exists |

!!! note "Known gaps"
    TC-22 (full team detail with member profiles), TC-23 (edit team), and
    TC-28 (delete team) are the three requirements without a working
    end-user endpoint today. Additionally, the notification mechanism used
    by TC-25 and TC-27 (`NotificationAdapter`) is currently a **stub** that
    only logs events — see [Appendices](anexos.md) for detail.

## Non-functional requirements

| ID | Requirement |
|---|---|
| NFR-01 | **Authentication delegated to Identity**: every protected endpoint depends on Identity Service being reachable; if it's down, no protected endpoint in Teams can authenticate anyone. |
| NFR-02 | **Data integrity**: team names are unique platform-wide; a team cannot exceed 12 members; a team needs at least 7 members to be enrolled in a tournament; duplicate pending invitations or transfer requests are rejected. |
| NFR-03 | **Resilience against unavailable external services**: if Tournament Service doesn't respond, enrollment and team listing fail safely (`502 Bad Gateway`); the active-tournament check fails open (returns `false`) instead of blocking the caller. |
| NFR-04 | **Maintainability**: business logic is decoupled from REST controllers and external integration details behind ports/adapters (hexagonal architecture). |
| NFR-05 | **Build reproducibility**: the project builds, tests, and packages deterministically via the Maven Wrapper, both locally and in CI. |
| NFR-06 | **Test coverage**: JaCoCo line-coverage gate enforced in the `verify` phase, with DTOs, MongoDB documents, repositories, and the stub `NotificationAdapter` excluded from the metric. |
| NFR-07 | **Network-level protection for service-to-service endpoints**: `GET /teams/{teamId}`, `.../by-player/{playerId}/roster`, and `.../active-tournament` carry no application-level authentication — they rely entirely on being unreachable from outside the trusted internal network. |

## Technical prerequisites

To develop and run the service locally:

| Tool | Minimum version | Use |
|---|---|---|
| [Java (JDK)](https://adoptium.net/) | 21 | Compiling and running the service |
| [Docker](https://www.docker.com/) / Docker Compose | 24+ | MongoDB and the application container |
| [Git](https://git-scm.com/) | 2.x | Version control |
| Maven Wrapper (`mvnw`, included in the repo) | — | No local Maven install required |

To work on the documentation:

| Tool | Minimum version | Use |
|---|---|---|
| [Python](https://www.python.org/) | 3.9+ | Required by MkDocs |
| [MkDocs](https://www.mkdocs.org/) + [Material for MkDocs](https://squidfunk.github.io/mkdocs-material/) | — | Building the documentation site |

See [Configuration](configuracion.md) for installation steps for each tool
and the service's environment variables.
