# Todo Backend API Guide

## Overview
The Todo Backend provides a RESTful API to create, read, update, and delete todo items. It is built with Spring Boot 3, uses an in-memory H2 database by default, and exposes OpenAPI/Swagger documentation.

- Base URL: http://localhost:3001
- Version: 0.1.0
- Authentication: None
- Content-Type: application/json
- OpenAPI/Swagger UI: http://localhost:3001/swagger-ui.html
- OpenAPI JSON: http://localhost:3001/api-docs

## Quick Start
- Start the application (see README for Gradle commands).
- Browse Swagger UI at /swagger-ui.html.
- Health endpoint /health should return OK.
- H2 console is available at /h2-console (requires browser session; JDBC URL is printed below in the README).

## Data Model (Response)
A todo item is represented by the following JSON shape in responses:

```json
{
  "id": 1,
  "title": "Buy groceries",
  "description": "Milk, Eggs, Bread",
  "completed": false,
  "dueDate": "2025-12-31",
  "createdAt": "2025-01-01T12:34:56.789Z",
  "updatedAt": "2025-01-01T12:34:56.789Z"
}
```

Field details:
- id: number
- title: string (max 255)
- description: string (max 2000)
- completed: boolean
- dueDate: string (ISO date, yyyy-MM-dd)
- createdAt: string (ISO instant)
- updatedAt: string (ISO instant)

## Error Response
The API returns structured errors:

```json
{
  "timestamp": "2025-01-01T12:34:56.789Z",
  "status": 400,
  "message": "Validation failed",
  "errors": [
    "title: title is required (rejected:  )",
    "description: description must be at most 2000 characters (rejected: ...)"
  ]
}
```

- status: HTTP status code (e.g., 400, 404).
- message: High-level error message or validation summary.
- errors: Optional array of field-level validation errors.

## Endpoints

### 1) Create a Todo
- Method: POST
- URL: /api/todos
- Request body:
  - title: required, non-blank, max 255
  - description: optional, max 2000
  - dueDate: optional, ISO date (yyyy-MM-dd)
- Responses:
  - 201 Created: returns the created todo
  - 400 Bad Request: validation error

Example request body:
```json
{
  "title": "Write docs",
  "description": "Draft API guide and README",
  "dueDate": "2025-12-31"
}
```

curl example:
```bash
curl -X POST http://localhost:3001/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Write docs","description":"Draft API guide and README","dueDate":"2025-12-31"}'
```

HTTPie example:
```bash
http POST :3001/api/todos title="Write docs" description="Draft API guide and README" dueDate="2025-12-31"
```

### 2) List Todos (with filters)
- Method: GET
- URL: /api/todos
- Query parameters:
  - completed: optional boolean (true/false)
  - q: optional string to search title/description (case-insensitive)
- Responses:
  - 200 OK: array of todos

Examples:
- List all:
```bash
curl http://localhost:3001/api/todos
```

- Filter by completed:
```bash
curl "http://localhost:3001/api/todos?completed=true"
```

- Search by text:
```bash
curl "http://localhost:3001/api/todos?q=docs"
```

Notes:
- There is no pagination implemented; the API returns all matching todos ordered by createdAt desc.

### 3) Get Todo by ID
- Method: GET
- URL: /api/todos/{id}
- Responses:
  - 200 OK: the todo
  - 404 Not Found: when id does not exist

Example:
```bash
curl http://localhost:3001/api/todos/1
```

### 4) Replace a Todo (PUT)
- Method: PUT
- URL: /api/todos/{id}
- Request body:
  - title: required, non-blank, max 255
  - description: optional, max 2000
  - completed: optional boolean
  - dueDate: optional ISO date
- Behavior:
  - Replaces the entire todo. If title is missing or blank, returns 400 with an error indicating "title is required for PUT".
- Responses:
  - 200 OK: updated todo
  - 400 Bad Request: validation or missing title
  - 404 Not Found

Example request body:
```json
{
  "title": "Updated title",
  "description": "Updated description",
  "completed": true,
  "dueDate": "2025-12-31"
}
```

curl example:
```bash
curl -X PUT http://localhost:3001/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated title","description":"Updated description","completed":true,"dueDate":"2025-12-31"}'
```

### 5) Partially Update a Todo (PATCH)
- Method: PATCH
- URL: /api/todos/{id}
- Request body (all fields optional):
  - title: optional, max 255
  - description: optional, max 2000
  - completed: optional boolean
  - dueDate: optional ISO date
- Behavior:
  - Only provided fields are updated.
- Responses:
  - 200 OK: updated todo
  - 400 Bad Request: validation error (e.g., title too long)
  - 404 Not Found

Example request body:
```json
{
  "description": "Patched description",
  "completed": false
}
```

curl example:
```bash
curl -X PATCH http://localhost:3001/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"description":"Patched description","completed":false}'
```

### 6) Toggle Completion
- Method: PATCH
- URL: /api/todos/{id}/complete
- Behavior: Flips completed between true/false.
- Responses:
  - 200 OK: updated todo
  - 404 Not Found

Example:
```bash
curl -X PATCH http://localhost:3001/api/todos/1/complete
```

### 7) Delete a Todo
- Method: DELETE
- URL: /api/todos/{id}
- Responses:
  - 204 No Content: on success
  - 404 Not Found

Example:
```bash
curl -X DELETE http://localhost:3001/api/todos/1
```

## Utility and Meta Endpoints

### Root Welcome
- GET /
- Returns a welcome message string.

### Health
- GET /health
- Returns "OK" if the app is responsive.

### Info
- GET /api/info
- Returns basic app info string.

### Swagger UI
- GET /docs
- Redirects to /swagger-ui.html using the request’s host and scheme.

### Actuator
- GET /actuator/health
- GET /actuator/info
- GET /actuator/metrics

### H2 Console
- GET /h2-console
- Requires browser access. See README for the JDBC URL.

## Validation Rules Summary
- Create (POST):
  - title: required, not blank, max 255
  - description: optional, max 2000
  - dueDate: optional ISO date
- Update (PUT):
  - title: required, not blank, max 255
  - description: optional, max 2000
  - completed: optional
  - dueDate: optional
- Patch (PATCH):
  - All fields optional; if present, same constraints as above

Validation errors return HTTP 400 with message "Validation failed" and field details in errors[].

## Examples

Create, then list, update, toggle, and delete:

```bash
# Create
curl -s -X POST :3001/api/todos \
  -H "Content-Type: application/json" \
  -d '{"title":"Write tests","description":"Cover service and controller","dueDate":"2025-12-31"}' | jq .

# List
curl -s :3001/api/todos | jq .

# PUT update id=1
curl -s -X PUT :3001/api/todos/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Updated title","description":"Updated description","completed":true}' | jq .

# Toggle completion
curl -s -X PATCH :3001/api/todos/1/complete | jq .

# Delete
curl -i -X DELETE :3001/api/todos/1
```

## Notes on Pagination and Filtering
- Pagination is not implemented. For large datasets you may implement paging later.
- Filters supported:
  - completed=[true|false]
  - q=free-text search in title and description (case-insensitive)

## Common Status Codes
- 200 OK: Successful GET/PUT/PATCH/Toggle.
- 201 Created: Successful creation.
- 204 No Content: Successful deletion.
- 400 Bad Request: Validation error or illegal input.
- 404 Not Found: Resource not found.

## Changelog Highlights
See the project README for release notes. In short, this release includes complete CRUD operations, validation, error handling, seed data (dev profile), and a test suite.
