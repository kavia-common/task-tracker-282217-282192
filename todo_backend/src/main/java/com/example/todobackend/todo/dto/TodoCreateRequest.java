package com.example.todobackend.todo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * PUBLIC_INTERFACE
 * Request body for creating a Todo.
 */
@Schema(description = "Request to create a new Todo")
public class TodoCreateRequest {

    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be at most 255 characters")
    @Schema(description = "Title of the todo", example = "Buy groceries", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 2000, message = "description must be at most 2000 characters")
    @Schema(description = "Detailed description", example = "Milk, Eggs, Bread, Butter")
    private String description;

    @Schema(description = "Due date", example = "2025-12-31")
    private LocalDate dueDate;

    public String getTitle() {
        return title;
    }

    public TodoCreateRequest setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public TodoCreateRequest setDescription(String description) {
        this.description = description;
        return this;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public TodoCreateRequest setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }
}
