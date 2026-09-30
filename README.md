# Users Management — Spring Boot, Arquitectura Hexagonal y DDD

Aplicación de gestión de usuarios construida con Java 17 y Spring Boot. La API REST es el punto de entrada activo. El código de la antigua CLI se conserva como adaptador inactivo y no posee un contenedor de dependencias independiente.

Spring es el único *composition root*: `Main` inicia el contexto y las dependencias se resuelven mediante configuración y component scanning de Spring.

---

## Ejecución con Docker (recomendado)

El sistema completo (aplicación + PostgreSQL + SMTP) se levanta con un solo comando.

### Prerequisitos

- [Docker](https://docs.docker.com/get-docker/) >= 20.10
- [Docker Compose](https://docs.docker.com/compose/install/) >= 2.0

### Levantar todo

```bash
docker compose up --build
```

Esto construye la imagen de la aplicación, levanta PostgreSQL, MailHog (SMTP de prueba) y la app. La base de datos se inicializa automáticamente con el esquema y un usuario administrador.

### Servicios y puertos

| Servicio  | Puerto  | Descripción                         |
|-----------|---------|-------------------------------------|
| App       | 8080    | API REST                            |
| PostgreSQL | 5432   | Base de datos                       |
| MailHog   | 8025    | Web UI para ver correos enviados    |
| MailHog   | 1025    | SMTP (para la app)                  |

### Verificar

```bash
# Crear usuario
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "id": "usr-001",
    "name": "Juan Perez",
    "email": "juan@example.com",
    "password": "Password123!",
    "role": "MEMBER"
  }'

# Listar usuarios
curl http://localhost:8080/api/users

# Swagger UI
# Abrir http://localhost:8080/swagger-ui.html
```

### Ver correos enviados

Abrir http://localhost:8025 para acceder a la interfaz de MailHog y ver los correos generados por la aplicación (bienvenida, actualización).

### Detener

```bash
docker compose down
```

### Eliminar datos (volumen de PostgreSQL)

```bash
docker compose down -v
```
---

## Ejecución local (sin Docker)

### Requisitos

- Java 17
- PostgreSQL 16 corriendo en `localhost:5432`
- Base de datos `crud_usuarios` creada (ejecutar `src/main/resources/schema.sql`)

### Build y test

```bash
./mvnw clean test
./mvnw clean package
```

En Windows se puede utilizar `mvnw.cmd`.

## PostgreSQL con Docker

Requisitos: Docker y Docker Compose.

Para levantar PostgreSQL, MailHog y la aplicación:

```bash
docker compose up -d --build
```

Servicios disponibles:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MailHog: `http://localhost:8025`
- PostgreSQL: `localhost:5432`, base `crud_usuarios`, usuario `postgres`, contraseña `postgres`

La configuración local usa PostgreSQL. Las variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME` y
`DB_PASSWORD` permiten conectarse a otra instancia. Para apagar los
servicios sin borrar los datos:

```bash
docker compose down
```

Para borrar también el volumen de PostgreSQL:

```bash
docker compose down -v
```

