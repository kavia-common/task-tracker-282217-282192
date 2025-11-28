package com.example.todobackend.todo.dto;

import com.example.todobackend.todo.Todo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.time.LocalDate;

/**
 * PUBLIC_INTERFACE
 * Response payload for Todo.
 */
@Schema(description = "Todo response")
public class TodoResponse {

    @Schema(description = "ID", example = "1")
    private Long id;

    @Schema(description = "Title", example = "Buy groceries")
    private String title;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Completion flag")
    private boolean completed;

    @Schema(description = "Due date", example = "2025-12-31")
    private LocalDate dueDate;

    @Schema(description = "Created timestamp (UTC instant)")
    private Instant createdAt;

    @Schema(description = "Updated timestamp (UTC instant)")
    private Instant updatedAt;

    public static TodoResponse fromEntity(Todo t) {
        TodoResponse r = new TodoResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setCompleted(t.isCompleted());
        r.setDueDate(t.getDueDate());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        return r;
    }

    public Long getId() {
        return id;
    }

    public TodoResponse setId(Long id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public TodoResponse setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TodoResponse setDescription(String description) {
        this.description = description;
        return this;
    }

    public boolean isCompleted() {
        return completed;
    }

    public TodoResponse setCompleted(boolean completed) {
        this.completed = completed;
        return this;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TodoResponse setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public TodoResponse setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public TodoResponse setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }
}
