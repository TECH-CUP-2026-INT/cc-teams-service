# teams-service

Microservicio de gestión de equipos para la plataforma universitaria **TechCup Fútbol**. Forma parte de un ecosistema de microservicios independientes y es responsable del ciclo de vida completo de un equipo: creación, invitaciones, transferencia de capitanía e inscripción en torneos.

---

## Integrantes

- Carlos Duban Rojas Riveros
- Juan Eduardo Vera Acero
- José Luis García Chinchilla
- Willian Santiago Ruiz Medina

---

## Descripción

`teams-service` es el microservicio encargado de todo lo relacionado con un equipo como unidad dentro de TechCup Fútbol. Garantiza que:

- El nombre de un equipo sea único en toda la plataforma.
- Los jugadores se unan a un equipo únicamente mediante un flujo explícito de invitación/aceptación.
- La capitanía pueda transferirse, ya sea por iniciativa del capitán o por solicitud de un jugador, siempre con aceptación explícita de ambas partes.
- Un equipo solo pueda inscribirse en un torneo si cumple el mínimo de integrantes requerido (≥ 7).
- Cada acción relevante quede registrada en un log de auditoría consultable por administradores.

La autenticación de los endpoints de usuario final se delega al **Identity Service**: este servicio valida cada JWT llamando remotamente a `POST /api/v1/token/validate` de Identity, sin verificar la firma del token de forma local.

---

## Funcionalidades

| Módulo | Funcionalidad |
|---|---|
| **Equipos** | Crear un equipo (el creador se convierte en capitán automáticamente) |
| **Invitaciones** | Enviar, listar y responder invitaciones a jugadores |
| **Capitanía** | Iniciar una transferencia de capitanía, aplicar a ella y responderla |
| **Torneos** | Listar equipos inscritos en un torneo e inscribir un equipo (proxy a `mk-tournament-service`) |
| **Servicio a servicio** | Exponer endpoints públicos de solo lectura para `mk-tournament-service` y `users-players-service` |
| **Auditoría** | Registrar y consultar el log de seguridad (solo `ADMIN`) |

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje / runtime | Java 21 |
| Framework | Spring Boot 3.5.6 |
| Build | Maven (wrapper incluido) |
| Persistencia | MongoDB 7 |
| Comunicación entre servicios | OpenFeign (Identity Service, Tournament Service) |
| API | Spring Web (REST, multipart) + springdoc-openapi (Swagger UI) |
| Seguridad | JWT validado remotamente vía Identity Service |
| Mapeo de objetos | MapStruct 1.6.3 + Lombok |
| Cobertura de pruebas | JaCoCo (mínimo 80 % de líneas) |
| CI/CD | GitHub Actions (build, test, imagen Docker, despliegue en Azure) |
| Documentación técnica | MkDocs + Material for MkDocs |

---

## Inicio rápido

### Prerrequisitos

- Java 21
- Docker y Docker Compose
- Git

### Clonar el repositorio

```bash
git clone https://github.com/TECH-CUP-2026-INT/cc-teams-service.git
cd cc-teams-service
```

### Opción 1 — Docker Compose (recomendado)

Levanta MongoDB y ejecuta el servicio contra él:

```bash
docker compose up -d
./mvnw spring-boot:run
```

### Opción 2 — Solo Maven local

Requiere una instancia de MongoDB accesible (local o en la nube):

```bash
export MONGODB_URI=mongodb://localhost:27017/teams_service
./mvnw spring-boot:run
```

> En Windows (PowerShell): `$env:MONGODB_URI="mongodb://localhost:27017/teams_service"`

### Opción 3 — Docker standalone

```bash
docker build -t cc-teams-service:latest .
docker run --rm -p 5622:5622 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/teams_service \
  -e IDENTITY_SERVICE_URL=http://identity-service:5620 \
  cc-teams-service:latest
```

El servicio queda disponible en `http://localhost:5622`.  
Explora la API en: [http://localhost:5622/swagger-ui.html](http://localhost:5622/swagger-ui.html)

### Ejecutar pruebas

```bash
./mvnw verify
```

Genera el reporte de cobertura JaCoCo en `target/site/jacoco/index.html`.

---

## Variables de entorno

| Variable | Valor por defecto | Descripción |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/teams_service` | Cadena de conexión a MongoDB |
| `SERVER_PORT` | `5622` | Puerto HTTP del servicio |
| `IDENTITY_SERVICE_URL` | `http://identity-service:5620` | URL base del Identity Service (validación de JWT) |
| `COMMUNICATIONS_URL` | `http://localhost:8083` | URL base del servicio de notificaciones |
| `TOURNAMENT_SERVICE_URL` | `http://localhost:8080` | URL base de `mk-tournament-service` |

> ⚠️ En entornos desplegados, las tres URLs de servicios externos **deben** sobreescribirse con las URLs reales. De lo contrario, el servicio arranca pero todas las llamadas entre servicios fallan.

---

## Documentación técnica

La documentación completa está construida con MkDocs. Para levantarla localmente:

```bash
pip install mkdocs-material
mkdocs serve
```

Disponible en [http://127.0.0.1:8000](http://127.0.0.1:8000).


