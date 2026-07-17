# Introduction

## Context

**TechCup Fútbol** is a university tournament whose digital platform is
made up of a set of independent microservices, each owning a bounded
business domain (identity, teams, tournaments, matches, notifications,
logistics, and so on).

The **Teams Service** (`service-teams`) is the microservice responsible for
everything related to a team as a unit: creating it, inviting players to
join it, handing off its captaincy, and enrolling it in tournaments.

## Purpose

Give captains and players a reliable way to organize a team for the
tournament, so that:

- A team's name is unique platform-wide, with a captain assigned at
  creation.
- Players are added to a team only through an explicit invite/accept flow,
  never unilaterally.
- Captaincy can change hands — either the current captain hands it off, or
  a player requests it — always requiring the other side's explicit
  acceptance.
- A team can only be enrolled in a tournament once it meets the minimum
  roster size, with `mk-tournament-service` as the final authority on
  tournament capacity and status.
- Every relevant action (team created, invitation sent/accepted/rejected,
  captaincy transferred, enrollment) is recorded for later audit.

## Actors

| Actor | Capabilities |
|---|---|
| **Captain** | Creates a team (becomes its captain), sends/lists invitations for their team, initiates a captaincy transfer, enrolls the team in a tournament |
| **Player** | Accepts/rejects invitations received, applies for captaincy of a team they belong to, responds to a captaincy transfer addressed to them |
| **Admin** | Queries the security audit log |
| **mk-tournament-service** | Calls `GET /teams/{teamId}` (service-to-service, unauthenticated) to read a team's name and roster size |
| **users-players-service** | Calls `GET /teams/by-player/{playerId}/roster` and `.../active-tournament` (service-to-service, unauthenticated) |

Authentication for end-user endpoints is delegated to **Identity Service**:
this service validates every JWT by calling Identity's
`POST /api/v1/token/validate` remotely — it does not verify a token's
signature itself. See [Architecture](arquitectura.md#security) for the
detail.

## Scope

### What this service DOES do

1. Create a team with a name, logo, and colors, assigning the creator as
   captain.
2. Send, list, and respond to team invitations.
3. Initiate, apply for, and respond to captaincy transfer requests.
4. List a tournament's registered teams and enroll a team in a tournament
   (proxying to `mk-tournament-service`).
5. Expose read-only, unauthenticated info endpoints for other
   microservices (roster size, member IDs, active-tournament status).
6. Record a security audit log of the actions above, queryable by `ADMIN`.

### What it does NOT do (owned by other services)

| Responsibility | Owning service |
|---|---|
| Tournament definitions, capacity, and enrollment status | mk-tournament-service |
| Player identity, profile data, and role/status source of truth | users-players-service / Identity Service |
| JWT signing and signature verification | Identity Service |
| Sending the actual invitation/captaincy-transfer notification | Communications (currently a logging stub — see [Appendices](anexos.md)) |

See [Architecture](arquitectura.md) for how this service talks to each of
them.
