package com.example.todobackend.todo;

import com.example.todobackend.exception.ErrorResponse;
import com.example.todobackend.todo.dto.TodoCreateRequest;
import com.example.todobackend.todo.dto.TodoResponse;
import com.example.todobackend.todo.dto.TodoUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * PUBLIC_INTERFACE
 * REST controller exposing CRUD operations for Todos.
 */
@RestController
@RequestMapping(value = "/api/todos", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Todos", description = "CRUD operations for Todo items")
public class TodoController {

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    @Operation(summary = "Create a new Todo",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Created",
                            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse create(@Valid @RequestBody TodoCreateRequest req) {
        return TodoResponse.fromEntity(service.create(req));
    }

    @Operation(summary = "List Todos with optional filters",
            parameters = {
                    @Parameter(name = "completed", description = "Filter by completion status"),
                    @Parameter(name = "q", description = "Search query on title/description")
            },
            responses = @ApiResponse(responseCode = "200", description = "List of todos",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TodoResponse.class)))))
    @GetMapping
    public List<TodoResponse> list(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String q
    ) {
        return service.list(completed, q).stream().map(TodoResponse::fromEntity).toList();
    }

    @Operation(summary = "Get a Todo by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Found",
                            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{id}")
    public TodoResponse getById(@PathVariable Long id) {
        return TodoResponse.fromEntity(service.getById(id));
    }

    @Operation(summary = "Replace a Todo (PUT)",
            description = "Replaces the entire Todo. Title is required.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated",
                            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TodoResponse putUpdate(@PathVariable Long id, @Valid @RequestBody TodoUpdateRequest req) {
        return TodoResponse.fromEntity(service.update(id, req, true));
    }

    @Operation(summary = "Partially update a Todo (PATCH)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Updated",
                            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public TodoResponse patchUpdate(@PathVariable Long id, @Valid @RequestBody TodoUpdateRequest req) {
        return TodoResponse.fromEntity(service.update(id, req, false));
    }

    @Operation(summary = "Toggle completion state of a Todo",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Toggled",
                            content = @Content(schema = @Schema(implementation = TodoResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PatchMapping("/{id}/complete")
    public TodoResponse toggleComplete(@PathVariable Long id) {
        return TodoResponse.fromEntity(service.toggleComplete(id));
    }

    @Operation(summary = "Delete a Todo",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deleted"),
                    @ApiResponse(responseCode = "404", description = "Not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
