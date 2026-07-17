# API

## Interactive documentation (Swagger UI)

With the service running:
[http://localhost:5622/swagger-ui.html](http://localhost:5622/swagger-ui.html)

The raw OpenAPI specification is available at `/v3/api-docs`.

## Authentication

This service does not verify a JWT's signature itself — it forwards the
`Authorization` header to Identity Service's `POST /api/v1/token/validate`
on every request and trusts the response. See
[Architecture](arquitectura.md#security) for the detail.

To try protected endpoints from Swagger UI, use the **Authorize** button
with a real JWT obtained from Identity Service's login flow (it must
actually validate there, since Teams Service re-checks it remotely on every
call — unlike a self-contained JWT setup, a hand-crafted token won't work
here).

## Endpoints

### Teams

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/teams` (multipart) | Required | Create a team; the caller becomes its captain |

### Invitations

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/invitations/teams/{teamId}` | Required (captain) | Send an invitation to a player |
| `PUT` | `/api/v1/invitations/{invitationId}/respond` | Required (invited player) | Accept or reject an invitation |
| `GET` | `/api/v1/invitations/my` | Required | List invitations received by the caller |
| `GET` | `/api/v1/invitations/teams/{teamId}` | Required (captain) | List invitations sent by the caller's team |

### Captaincy

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/captaincy/teams/{teamId}/transfer` | Required (captain) | Initiate a transfer to a chosen teammate |
| `POST` | `/api/v1/captaincy/teams/{teamId}/apply` | Required (team member) | Apply for captaincy of the caller's own team |
| `PUT` | `/api/v1/captaincy/{transferId}/respond` | Required (designated recipient) | Accept or reject a transfer request |

### Tournaments

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/v1/tournaments/{tournamentId}/teams` | Required | List teams registered in a tournament (proxy to Tournament Service) |
| `POST` | `/api/v1/tournaments/{tournamentId}/teams/{teamId}/enrollment` | Required (captain) | Enroll the team in a tournament (requires ≥ 7 members) |

### Audit

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/api/v1/audit` | Required (`ADMIN`) | Query the security audit log, filterable by date range, action type, and team |

### Service-to-service (no `/api/v1` prefix, unauthenticated)

| Method | Path | Consumed by | Description |
|---|---|---|---|
| `GET` | `/teams/{teamId}` | mk-tournament-service | Team name and roster size |
| `GET` | `/teams/by-player/{playerId}/roster` | users-players-service | Team ID and member IDs for a player (used to validate jersey-number uniqueness) |
| `GET` | `/teams/by-player/{playerId}/active-tournament` | users-players-service | Whether the player's team has an active tournament enrollment; fails open (`false`) if Tournament Service is unreachable |

## Example: create a team

`POST /api/v1/teams` (multipart request)

Part `team` (JSON, `CreateTeamRequest`):

```json
{
  "name": "Los Ingenieros FC",
  "captainName": "Camila Rodríguez",
  "colors": "Blue and white"
}
```

Part `logo`: an image file.

Response `201 Created` (`TeamResponse`):

```json
{
  "id": "1f2e3d4c-5b6a-4978-8a9b-0c1d2e3f4a5b",
  "name": "Los Ingenieros FC",
  "colors": "Blue and white",
  "captainId": "a1c3d4e5-1234-4a5b-8c9d-0e1f2a3b4c5d",
  "memberCount": 1,
  "createdAt": "2026-07-12T20:12:45Z"
}
```

A second `POST` with the same `name` responds `409 Conflict`
(`TeamNameAlreadyExistsException`).

## Example: enroll a team in a tournament

`POST /api/v1/tournaments/{tournamentId}/teams/{teamId}/enrollment`

Requires the caller to be the team's captain, and the team to have at least
7 members (`Team.MIN_MEMBERS_FOR_TOURNAMENT`). Tournament Service
additionally validates capacity and tournament status.

Response `201 Created` (`TournamentEnrollmentResponse`):

```json
{
  "enrollmentId": "3c1d2e3f-4a5b-4978-8a9b-0c1d2e3f4a5b",
  "status": "CONFIRMED",
  "reservationExpiresAt": null
}
```

If the team has fewer than 7 members, the request never reaches Tournament
Service — it fails locally with `TeamNotEligibleForTournamentException`. If
Tournament Service rejects the enrollment (capacity full, tournament
closed), the response is `409 Conflict`
(`TournamentEnrollmentRejectedException`) with the reason Tournament
Service returned.

## Errors

Business errors are handled centrally in `GlobalExceptionHandler`
(`@RestControllerAdvice`) and returned as a JSON object with an error code
and message, with an appropriate HTTP status:

| Code | Cause |
|---|---|
| `400` | Payload validation (Bean Validation), malformed JSON, invalid request parameter, missing required header |
| `401` | Not authenticated (missing or invalid JWT) |
| `403` | Authenticated but not authorized for that action (`UnauthorizedTeamActionException`, `AccessDeniedException`) |
| `404` | Resource not found (`TeamNotFoundException`, `InvitationNotFoundException`, `TransferNotFoundException`, `TournamentNotFoundException`) |
| `409` | Conflict (`TeamNameAlreadyExistsException`, `PlayerAlreadyInTeamException`, `TeamFullException`, `TournamentEnrollmentRejectedException`) |
| `415` | Unsupported `Content-Type` |
| `502` | Tournament Service didn't respond (`TournamentServiceUnavailableException`) |
