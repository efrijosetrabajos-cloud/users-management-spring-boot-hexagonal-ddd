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

## Secretos para desarrollo local

Copiar `application-secrets.properties.example` como
`.environments/application-secrets.properties` y reemplazar los valores de ejemplo.
Este archivo contiene la conexion local a la base de datos, la semilla JWT y las credenciales de
Brevo. La carpeta `.environments` esta excluida de Git y del contexto de Docker.

En produccion no se utiliza este archivo: los secretos deben configurarse mediante las
variables de entorno `DB_*`, `JWT_SECRET` y `BREVO_*` del proveedor de despliegue.

## PostgreSQL con Docker

Requisitos: Docker y Docker Compose.

Para levantar PostgreSQL y la aplicación:

```bash
docker compose up -d --build
```

Servicios disponibles:

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- PostgreSQL: `localhost:5432`, base `crud_usuarios`, usuario `postgres`, contraseña `postgres`

La configuración local usa PostgreSQL. Las variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`,
`DB_PASSWORD` y `DB_SSLMODE` permiten conectarse a otra instancia.

Para apagar los servicios sin borrar los datos:

```bash
docker compose down
```

Para borrar también el volumen de PostgreSQL:

```bash
docker compose down -v
```

## Notificaciones por correo (Brevo)

Los correos transaccionales se envian con `POST /v3/smtp/email` de Brevo. La llamada
se ejecuta fuera del hilo HTTP en el executor `notificationExecutor` y dispone de
timeouts, reintentos para errores de red/408/429/5xx con backoff exponencial, circuit
breaker e idempotencia para evitar duplicados durante los reintentos.

Variables requeridas: `BREVO_API_KEY` y `BREVO_FROM_ADDRESS`. El remitente debe estar
verificado en Brevo. Los parametros de resiliencia `BREVO_CONNECT_TIMEOUT`,
`BREVO_READ_TIMEOUT`, `BREVO_RETRY_*` y `BREVO_CIRCUIT_*` son opcionales y tienen
valores predeterminados en `application.properties`.
