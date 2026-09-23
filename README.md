# Ventas-Backend

API REST de un **sistema de ventas (despensa)** construido con **Spring Boot 4**. Incluye catálogo de productos, caja/POS, ventas, pagos con tarjeta, compras a proveedores, inventario, roles y permisos (JWT) y reportes estadísticos.

Frontend Angular disponible en el repositorio hermano **Ventas-Frontend**.

## Stack

- **Spring Boot 4.0.5** · **Java 21** · Maven (`mvnw.cmd`)
- **PostgreSQL** (H2 para tests · Spring Data JPA / Hibernate, `ddl-auto: update`)
- **Spring Security + JWT** (HS256, expiración 5 h, secret desde `JWT_SECRET`)
- **spring-boot-starter-mail** (Gmail SMTP para recuperación de contraseña)
- Lombok, Bean Validation, multipart (`uploads/`, máx. 20 MB)
- **Terminal de pagos**: `SIMULATED` (modo demo) o `PHYSICAL` (Socket TCP)

## Características

- Autenticación JWT con recuperación/cambio de contraseña por email.
- **RBAC**: 3 roles base (ADMIN, CAJERO, ALMACENISTA) y **34 permisos** verificados con `@PreAuthorize`.
- **Productos**: CRUD con imagen, búsqueda, filtro por categoría, stock bajo y **borrado lógico** (dar de baja/reactivar) para conservar el histórico.
- **Ventas**: en efectivo, transferencia o tarjeta.
- **Pagos**: estados PENDING/PROCESSING/APPROVED/REJECTED/REVERSED, reintentos (máx. 3), reversas, idempotencia y monitor automático de pagos pendientes.
- **Compras y proveedores**: al registrar una compra suma stock y guarda el costo real del producto; la cancelación revierte stock.
- **Caja**: apertura, cierre y corte.
- **Reportes**: tendencia de ventas, top productos, métodos de pago, desempeño por categoría, stock bajo, márgenes y resumen — agregados en SQL.
- **Seed inicial** controlable: roles, permisos, admin y asignaciones (flag `APP_SEED_BOOTSTRAPS`).
- **130 tests** en verde (repositorios, servicios, almacenamiento de archivos, controllers con MockMvc y bootstraps).

## Requisitos

- Java 21
- Maven (o usar `mvnw.cmd`)
- PostgreSQL en `localhost:5432` con una base `ventas_db` (u otra según `DB_NAME`)

## Configuración

Todos los valores sensibles se inyectan por **variables de entorno** (no hay secretos en el repositorio). En local se resuelven desde `src/main/resources/application-local.yaml` (archivo ignorado por git). En producción, desde las variables del servicio.

| Variable | Descripción | Default |
|---|---|---|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | Conexión a PostgreSQL | `localhost` / `5432` / `ventas_db` |
| `DB_USER` / `DB_PASSWORD` | Credenciales de BD | — (obligatorias) |
| `JWT_SECRET` | Clave HS256 para firmar tokens (≥ 32 bytes) | — (obligatoria) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | SMTP (Gmail) para recuperar contraseña | — (obligatorias) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Usuario administrador inicial | — (obligatorias) |
| `PAYMENT_MERCHANT_ID` / `PAYMENT_TERMINAL_ID` / `PAYMENT_KEYSTORE_PASSWORD` | Credenciales de la terminal de pago | — |
| `PAYMENT_TERMINAL_TYPE` | `SIMULATED` o `PHYSICAL` | `SIMULATED` |
| `CORS_ALLOWED_ORIGINS` | Orígenes permitidos (separados por coma) | — |
| `FRONTEND_URL` | URL del frontend para emails | `http://localhost:4200` |
| `UPLOAD_DIR` | Carpeta física de imágenes | `./uploads` |
| `UPLOAD_URL` | URL base pública de imágenes | `http://localhost:8081/api/uploads` |
| `PORT` | Puerto del servidor | `8081` |
| `JPA_DDL_AUTO` | `update` (dev) o `validate` (prod madura) | `update` |
| `APP_SEED_BOOTSTRAPS` | `true` siembra roles/permisos/admin si faltan | `true` |
| `SHOW_SQL` | Mostrar SQL en logs | `true` |

> **Importante**: `application-local.yaml` **no está versionado** y es necesario en local. Si agregas un `${VAR}` nuevo a `application.yaml`, agrégalo también ahí (o en las variables de Railway).

## Cómo ejecutar

```sh
# Compilar
.\mvnw.cmd compile

# Ejecutar en http://localhost:8081 (requiere PostgreSQL y application-local.yaml)
.\mvnw.cmd spring-boot:run

# Tests (requiere PostgreSQL levantado en local)
.\mvnw.cmd test
```

### Perfil local

`spring.profiles.default` es `local`, así que al arrancar se carga `application-local.yaml`. Crea ese archivo a partir del ejemplo documentado en `application.yaml` (donde viven los valores reales de dev).

### Acceso inicial

Si la BD está vacía y `APP_SEED_BOOTSTRAPS=true`, los 4 bootstraps crean roles (ADMIN, CAJERO, ALMACENISTA), los permisos, el admin y sus asignaciones.

## Estructura

```
src/main/java/com/erikjarquin/ventas/
├── controller/      # 12 controllers REST (57 endpoints bajo /api)
├── service/         # Interfaces y service/impl/
├── repository/      # Spring Data JPA (queries JPQL agregadas para reportes)
├── model/
│   ├── entity/      # Entidades JPA
│   ├── dto/         # DTOs (incl. model/dto/Reports)
│   └── enums/       # PermissionName, PaymentStatus, PaymentMethod, ReportGroup...
├── mapper/          # Entity <-> DTO
├── config/          # Seguridad, Web, JWT, CORS
├── exceptions/      # Excepciones + GlobalExceptionHandler
└── jobs/            # PaymentMonitorJob (pagos PENDING > 5 min)
```

## Endpoints principales

| Módulo | Base | Notas |
|---|---|---|
| Auth | `/api/auth` | login, forgot/reset/change-password, me/authorities — público |
| Users | `/api/users` | CRUD + `PATCH /{id}/active` |
| Roles | `/api/roles` | CRUD |
| Permissions | `/api/permissions` | GET all |
| Products | `/api/products` | CRUD multipart, `/barcode/{bc}`, `/search?q=`, `?category=`, `/inactive`, `PATCH /{id}/deactivate`, `PATCH /{id}/active` |
| Categories | `/api/categories` | CRUD |
| Sales | `/api/sales` | POST (efectivo/transf/tarjeta), GET |
| Payments | `/api/payments` | card, status/{tx}, retry/{id}, reverse/{id} |
| Purchases | `/api/purchases` | GET (todos/proveedor/detalle), POST, DELETE (cancelar) |
| Providers | `/api/providers` | CRUD |
| Cash | `/api/cash` | open, close, summary, active |
| Reports | `/api/reports` | trend, top-products, payment-methods, categories, low-stock, margins, summary |
| Uploads | `/api/uploads/**` | Público — sirve imágenes de productos |

Autenticación: `Authorization: Bearer <jwt>`. Rutas públicas: `OPTIONS /**`, `/api/auth/**`, `/api/uploads/**`. Cada endpoint de negocio exige el permiso correspondiente (`@PreAuthorize("hasAuthority('...')")`).

## Despliegue (Railway)

1. Sube el repositorio a GitHub (purga antes el historial de git con `git filter-repo` si contiene secretos).
2. Railway → New Project → Deploy from GitHub repo (usa el `Dockerfile`).
3. Define las **variables de entorno** obligatorias de la tabla de arriba.
4. Añade un **Railway Postgres** y apunta `DB_HOST`/`DB_PORT`/`DB_USER`/`DB_PASSWORD`/`DB_NAME` a ese servicio.
5. Crea un **Volumen** montado en `/app/uploads` y define `UPLOAD_DIR=/app/uploads`, `UPLOAD_URL=https://<tu-app>.up.railway.app/api/uploads`.
6. En el frontend (Netlify) define `environment.prod.ts` con la API `https://<backend>.up.railway.app/api`.

## Licencia

Privado — uso interno del proyecto.