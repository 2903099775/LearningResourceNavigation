# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

```bash
# Compile
mvn compile -q

# Run (restarts on code change)
mvn spring-boot:run

# Full restart (Windows PowerShell)
Get-Process java -ErrorAction SilentlyContinue | Stop-Process -Force
mvn compile -q
mvn spring-boot:run

# Run tests
mvn test
mvn test -Dtest=AuthServiceTest

# Package
mvn clean package -DskipTests
```

- **Java 21**, **Spring Boot 3.2.5**, **Maven**, **MySQL 8.0** (database: `learning_nav`, user: `root`/`123456`)
- Port **8080**, only binds `127.0.0.1` (not externally accessible by default)
- Homepage: http://localhost:8080 | Admin: http://localhost:8080/admin
- Templates are served from `target/classes/templates/` — Maven copies them on compile, but if you edit templates in `src/`, you must recompile (or copy manually) for changes to take effect.

## Architecture

**Layered**: Controller → Service (interface + impl) → Mapper (MyBatis interface + XML) → MySQL

**Tech stack**: Spring Boot + MyBatis + Thymeleaf + Spring Security 6 + JWT + Caffeine cache. No frontend framework — every page is a standalone Thymeleaf template with inline `<style>` and `<script>` blocks. REST API calls from JavaScript update the DOM.

### Five-Level Learning Resource Hierarchy

```
LearningCategory (领域) → LearningSubcategory (子域) → LearningPath (路线) → PathStage (阶段) → LearningUnit (单元)
```

This is the core domain model. UI labels must match exactly: 学习领域 → 学习子域 → 学习路线 → 学习阶段 → 学习单元.

### Package Layout

| Package | Purpose |
|---------|---------|
| `controller/` | 21 public REST/page controllers + 9 admin controllers under `controller/admin/` |
| `service/` | Interfaces + `impl/` subpackage with implementations |
| `mapper/` | MyBatis interfaces; XML SQL in `src/main/resources/mapper/*.xml` |
| `entity/` | 21 POJOs, mostly Lombok `@Data`, some with transient fields for frontend enrichment |
| `config/` | Security, JWT filter, CORS, WebMVC (resource handlers), file upload, Jackson, cache |
| `common/` | `ResponseResult<T>` — unified API response `{code, message, data}` |
| `exception/` | `GlobalExceptionHandler` (@RestControllerAdvice) |

### API Response Convention

Every REST endpoint returns `ResponseResult<T>`. Frontend JavaScript checks `data.code === 200` to determine success. Non-200 codes are errors (401 unauthorized, 403 forbidden, 404 not found, 500 server error).

### Authentication

- **Stateless JWT**: `Authorization: Bearer <token>` header, validated by `JwtAuthenticationFilter` (a `OncePerRequestFilter`).
- **Dual login**: Regular users at `POST /api/public/auth/login`, admins at `POST /api/admin/auth/login`. Admin login rejects non-ADMIN role users.
- **Password storage**: Uses `NoOpPasswordEncoder` (plaintext). This is a known security gap. When adding features, do not rely on passwords being hashed.
- **User status**: `users.status` must be `1` for the JWT filter to authenticate. Disabled users get 403.
- **VIP**: Checked via `user.vipExpireDate` compared to `new Date()`. Users with expired VIP dates are treated as non-VIP.

### Frontend Patterns

- Each template has its own `<style>` and `<script>` — no shared CSS/JS framework.
- Login state: `localStorage.token` (JWT), `localStorage.user` (user object as JSON). Admin uses `localStorage.adminToken`.
- `checkLoginStatus()` + `updateUserMenu()` pattern: fetch `/api/user/profile` with the token, store user in `localStorage`, render nav avatar/dropdown.
- Avatar display: check `user.avatar` — if set, render `<img>` in `.user-avatar` div; otherwise show first letter of username.
- All pages have a unified hover-dropdown on the nav avatar with user info, VIP badge, and links.

### Static Resources & File Uploads

- **Avatars**: uploaded to `./uploads/avatars/`, served at `/avatars/**` via `WebMvcConfig` resource handler using `Path.toUri()` for correct cross-platform file URIs.
- **Post images**: uploaded to `./uploads/posts/`, served at `/posts/images/**`.
- Max upload: 10MB (set in both `application.properties` and controller code).
- Classpath static resources: `/js/**`, `/css/**`, `/images/**`, `/fonts/**` (many directories exist but may be empty — CSS is typically inline in templates).

### Key Gotchas

1. **Windows path handling**: Always use `Path.toUri().toString()` for file resource locations, never string concatenation with `file:` prefix. Windows backslashes break Spring's resource resolution.

2. **Template sync**: After editing files in `src/main/resources/templates/`, run `mvn compile` or copy files to `target/classes/templates/`. The running app serves from `target/`.

3. **Controller vs resource handler priority**: `@GetMapping` in controllers takes precedence over resource handlers. Never use catch-all patterns like `@GetMapping("/**")` in `PageController` — it will intercept static resources like `/avatars/**` and `/css/**`.

4. **404 handling**: Place error pages in `templates/error/404.html` (Spring Boot auto-discovers them). Do not implement 404 via controller catch-all mapping.

5. **Readme is outdated**: The README references a different database name (`learning_resource_db`) and wrong API paths (e.g., `/api/auth/register` instead of actual `/api/public/auth/register`). The schema file is at `db/schema-mysql.sql`. Consult actual controllers for correct routes.

6. **admin.html is an SPA**: The admin panel is a pure HTML/JS single-page app using REST APIs — no Vue/React.
