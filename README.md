# Task Tracker - todo_backend

## Project Overview
The todo_backend is a Spring Boot REST API for managing todo items. It exposes CRUD endpoints, validation, structured error handling, and a simple in-memory H2 database for local development. OpenAPI (Swagger UI) is included for interactive API exploration.

- Component: Backend (Spring Boot 3)
- Language: Java 17
- Port: 3001
- Database: H2 (in-memory)
- API docs: Swagger UI at /swagger-ui.html and OpenAPI JSON at /api-docs

## Tech Stack
- Spring Boot 3.4.x (Web, Data JPA, Validation, Actuator)
- H2 Database (in-memory)
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito (tests)
- Gradle 8.x
- Checkstyle (basic rules)

## Prerequisites
- Java 17 installed
- Internet access to download dependencies

No environment variables are required for local development.

## How to Run Locally
From the todo_backend directory:

```bash
./gradlew bootRun
```

The application starts on:
- http://localhost:3001

Alternatively, to build and run the jar:

```bash
./gradlew clean build
java -jar build/libs/todobackend-0.1.0.jar
```

## Profiles
- dev: Enables seed data via DataSeeder. To enable:
  - Add: `--spring.profiles.active=dev` when starting.
  - Example:
    ```bash
    ./gradlew bootRun --args='--spring.profiles.active=dev'
    ```
- default (no explicit profile): No seed data.

## Endpoints and Access
- Swagger UI: http://localhost:3001/swagger-ui.html
- Redirect to Swagger: http://localhost:3001/docs
- OpenAPI JSON: http://localhost:3001/api-docs
- Health: http://localhost:3001/health
- Actuator: 
  - http://localhost:3001/actuator/health
  - http://localhost:3001/actuator/info
  - http://localhost:3001/actuator/metrics
- H2 Console: http://localhost:3001/h2-console
  - JDBC URL: `jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE`
  - Username: `sa`
  - Password: (empty)

## API Quick Reference
All endpoints are unauthenticated and use application/json.

- POST /api/todos — create a todo (201)
- GET /api/todos — list todos; filters: completed=[true|false], q=search
- GET /api/todos/{id} — get by id (200, 404)
- PUT /api/todos/{id} — replace; title required (200, 400, 404)
- PATCH /api/todos/{id} — partial update; fields optional (200, 400, 404)
- PATCH /api/todos/{id}/complete — toggle completion (200, 404)
- DELETE /api/todos/{id} — delete (204, 404)

See docs/API_GUIDE.md for detailed request/response schemas and examples.

## Build and Test
- Build:
  ```bash
  ./gradlew clean build
  ```
- Run tests:
  ```bash
  ./gradlew test
  ```
- Checkstyle reports are generated under build/reports/checkstyle.

## Coding Standards
- Java 17
- Basic Checkstyle rules (outer type filename, empty block, braces, unused imports).
- Prefer clear method and variable names and keep controller/services thin and focused.
- Validation via jakarta.validation annotations on DTOs.
- Centralized error handling via ControllerAdvice.

## Folder Structure
- todo_backend/
  - src/main/java/com/example/todobackend
    - TodoBackendApplication.java — Spring Boot entry point and OpenAPI metadata
    - HelloController.java — "/" welcome, "/docs" redirect, "/health", "/api/info"
    - config/DataSeeder.java — seeds sample data when "dev" profile is active
    - exception/ — ErrorResponse, GlobalExceptionHandler, NotFoundException, AdditionalExceptionAdvice
    - todo/ — Entity, Repository, Service, Controller
      - dto/ — TodoCreateRequest, TodoUpdateRequest, TodoResponse
  - src/main/resources/application.properties — port=3001, H2, springdoc, actuator config
  - src/test/java/... — unit and integration tests
  - build.gradle — dependencies and plugins
  - config/checkstyle/checkstyle.xml — minimal rules
  - docs/API_GUIDE.md — API reference (this file)

## Contribution Guidelines
- Create a feature branch from main.
- Include or update tests for new functionality.
- Run `./gradlew test` and ensure all tests pass.
- Keep API docs in docs/API_GUIDE.md in sync with code changes.
- Follow Checkstyle guidelines and keep code self-documented.
- Open a PR describing changes, rationale, and testing notes.

## Changelog / Release Notes
### 0.1.0
- Package and build fix with Gradle wrapper and Spring Boot plugin alignment.
- CRUD Features:
  - Create, List (filters: completed, q), Get by ID, PUT replace, PATCH partial, Toggle completion, Delete.
- Validation:
  - @NotBlank and @Size constraints on DTOs; PUT requires title.
- Error Handling:
  - GlobalExceptionHandler for validation errors (400) with structured ErrorResponse.
  - NotFoundException mapped to 404.
  - AdditionalExceptionAdvice maps IllegalArgumentException to 400 for PUT missing title.
- Dev Seed Data:
  - DataSeeder seeds a few todos when the "dev" profile is active and repository is empty.
- Test Suite:
  - Unit tests for service and controller.
  - Controller validation tests.
  - Integration tests covering end-to-end CRUD, filters, validation, and toggle completion.
- Documentation:
  - Swagger UI via springdoc at /swagger-ui.html.
  - API guide and README.

## License
This repository is for demonstration and internal use. Add your preferred license if open-sourcing.