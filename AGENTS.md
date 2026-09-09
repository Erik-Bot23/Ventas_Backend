# Ventas-Backend — Contexto del Proyecto

> Este archivo registra el estado actual del proyecto y las decisiones tomadas. Se actualiza conforme avanzamos. Es el punto de referencia de contexto para continuar el trabajo desde cualquier máquina.

## Stack

- **Spring Boot 4.0.5** · Java 21 · Maven (`mvnw.cmd`)
- **PostgreSQL** `ventas_db` (localhost:5432, usuario `postgres` / `admin`)
- **Spring Data JPA** (Hibernate 7.2.7), `ddl-auto: update` (sin migraciones)
- **Spring Security + JWT** (jjwt 0.11.5, HS256, expiración 5h, secret hardcodeado ⚠️)
- **spring-boot-starter-mail** (Gmail SMTP para recuperar contraseña)
- **Lombok** (parcial), validación, multipart `uploads` (máx 20MB)
- **Server**: puerto `8081` · CORS solo para `http://localhost:4200`
- **Terminal de pagos**: `payment.terminal.type=SIMULATED` (default) o `PHYSICAL` (Socket TCP)

## Arquitectura

Capas: `controller/ → service/ (interfaz) → service/impl/ → repository/ → model/entity/`. DTOs en `model/dto/`, Mappers en `mapper/` (algunos estáticos, otros `@Component`), config en `config/`, excepciones en `exceptions/`.

- 10 controllers, 46 endpoints (`/api/...`)
- Autorización por permiso vía `@PreAuthorize("hasAuthority('...')")` (31 permisos en `PermissionName`)
- 4 `CommandLineRunner` de bootstrap (orden): `RoleBootstrap` → `AdminBootstrap` → `PermissionBootstrap` → `RolePermissionBootstrap`
- Admin inicial: `18jarquinsanchezerik1a@gmail.com` / `1234`

## Endpoints principales

| Módulo | Base | Notas |
|---|---|---|
| Auth | `/api/auth` | login, forgot/reset/change-password, me/authorities — público |
| Users | `/api/users` | CRUD + `PATCH /{id}/active` |
| Roles | `/api/roles` | CRUD |
| Permissions | `/api/permissions` | GET all |
| Products | `/api/products` | CRUD multipart, `/barcode/{bc}`, `/search?q=`, `?category=` |
| Categories | `/api/categories` | CRUD |
| Sales | `/api/sales` | POST (efectivo/transit/tarjeta), GET |
| Payments | `/api/payments` | card, status/{tx}, retry/{id}, reverse/{id} |
| Cash | `/api/cash` | open, close, summary, active |
| Uploads | `/api/uploads/**` | **Público** — sirve imágenes de productos |
| — | `GET /ping` | healthcheck (sin prefijo) |

## Estado de módulos

- **Terminal pagos**: abstracción `TerminalService` con `TerminalSimulatedImpl` (tarjetas de prueba DETERMINISTIC) y `TerminalPhysicalImpl` (Socket TCP, protocolo `PAY|`/`REV|`/`STS|`).
- **Pagos**: estados PENDING/PROCESSING/APPROVED/REJECTED/REVERSED, reintentos (máx 3), idempotencia, reversas. `PaymentMonitorJob` (`@Scheduled`, cada 5 min) revisa pagos PENDING > 5 min.
- **Frontend conectado**: Angular 21 standalone + SSR en `Ventas-Frontend` (ruta hermana), consume `http://localhost:8081/api`. Espera que `img` de productos sea **URL completa**.

## Registro de cambios / decisiones

### 2026-09-09 — Corrección de 3 bugs + ajuste imágenes
1. **`@EnableScheduling` agregado** en `VentasApplication.java` → `PaymentMonitorJob` ahora se ejecuta (antes estaba inactivo).
2. **`CategoryRepository.findByName`** corregido: retornaba `Optional<RoleEntity>` → ahora `Optional<CategoryEntity>` (e import eliminado).
3. **Almacenamiento real de imágenes**: nuevo `FileStorageService` (guarda en disco con nombre UUID + extensión), `WebConfig` sirve `/api/uploads/**`, `ProductImpl` guarda/actualiza/borra archivo físico junto al producto. Config: `app.upload-dir: ./uploads`.
4. **URL de imagen en DTO**: `ProductMapper` convertido a `@Component` (patrón de SaleMapper/PaymentMapper), inyecta `app.upload-url` y devuelve URL completa (`http://localhost:8081/api/uploads/<archivo>`). Frontend usaba `[src]="p.img"` directo → ahora resuelve. `ProductImpl` inyecta el mapper (ya no usa llamadas estáticas).

## Pendientes / issues conocidos

- ⚠️ **Credenciales hardcodeadas**: secret JWT, password PostgreSQL y app password Gmail están en `application.yaml`/código. Externalizar a env antes de producción.
- ⚠️ **`terminalsimulated_host` / configs de terminal** hardcodeadas (merchant-id, keystore).
- **Imágenes**: sin validación de tipo/contenido del archivo; sin perfil dev/prod (`application.yaml` único).
- **Tests**: solo `VentasApplicationTests` (contextLoads). Sin tests de controllers/servicios/repos.
- Frontend: componentes `Caja`, `Ventas`, `Clientes`, `Compras`, `Reportes`, `Facturas` son placeholders; `Salehistory` no ruteado.
- 26 archivos modificados sin commitear (trabajo en curso al momento de este registro).

## Cómo ejecutar

```sh
.\mvnw.cmd compile      # compilar
.\mvnw.cmd test         # tests (requiere PostgreSQL local levantado)
.\mvnw.cmd spring-boot:run
```