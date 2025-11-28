package com.example.todobackend.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * PUBLIC_INTERFACE
 * Request body for updating or partially updating a Todo.
 */
@Schema(description = "Request to update an existing Todo. All fields optional for PATCH; for PUT, title is required.")
public class TodoUpdateRequest {

    @Size(max = 255, message = "title must be at most 255 characters")
    @Schema(description = "Title of the todo", example = "Buy groceries")
    private String title;

    @Size(max = 2000, message = "description must be at most 2000 characters")
    @Schema(description = "Detailed description", example = "Milk, Eggs, Bread, Butter")
    private String description;

    @Schema(description = "Completion flag", example = "true")
    private Boolean completed;

    @Schema(description = "Due date", example = "2025-12-31")
    private LocalDate dueDate;

    public String getTitle() {
        return title;
    }

    public TodoUpdateRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TodoUpdateRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public TodoUpdateRequest setCompleted(Boolean completed) {
        this.completed = completed;
        return this;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TodoUpdateRequest setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }
}
