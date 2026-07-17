# Teams Service (service-teams)

Spring Boot microservice responsible for **team management** on the
university tournament **TechCup Fútbol**: team creation, invitations,
captaincy, and tournament enrollment. It is one of the platform's
independent microservices; its main actor is the **Captain**, with a
handful of endpoints reserved for the **Admin** and for service-to-service
calls from `mk-tournament-service` and `users-players-service`.

[View on GitHub](https://github.com/TECH-CUP-2026-INT/cc-teams-service){ .md-button .md-button--primary }
[Explore the API](api.md){ .md-button }

## Documentation map

| Section | Content |
|---|---|
| [Introduction](introduccion.md) | Context, purpose, and scope of the service |
| [Requirements](requerimientos.md) | Functional and non-functional requirements, technical prerequisites |
| [Configuration](configuracion.md) | Environment variables, running locally, and Docker deployment |
| [Architecture](arquitectura.md) | Layers, data model, and integrations with other services |
| [API](api.md) | REST endpoints, authentication, and Swagger UI |
| [Testing](pruebas.md) | Test strategy and how to run it |
| [Team](equipo.md) | TECH-CUP 2026 INT team members and roles |
| [Appendices](anexos.md) | Glossary, security notes, and references |

## Quick summary

| Layer | Technology |
|---|---|
| Language / runtime | Java 21 |
| Framework | Spring Boot 3.5.6 |
| Build | Maven |
| Persistence | MongoDB |
| Service-to-service | OpenFeign (Identity, Tournament) |
| API | Spring Web (REST, multipart) + springdoc-openapi |
| Security | JWT validated remotely via Identity Service (no local signing) |
| CI/CD | GitHub Actions (build, test, Docker image, deploy to Azure) |
| Documentation | MkDocs + Material for MkDocs |

## Quick start

```bash
docker compose up -d      # starts MongoDB
./mvnw spring-boot:run
```

With the service running, explore the API at
[http://localhost:5622/swagger-ui.html](http://localhost:5622/swagger-ui.html)
(port `5622` is this service's default, set in `application.yml`).

For more detail, see [Configuration](configuracion.md) and [API](api.md).
