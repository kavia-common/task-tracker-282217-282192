package com.example.todobackend.todo;

import com.example.todobackend.todo.dto.TodoCreateRequest;
import com.example.todobackend.todo.dto.TodoUpdateRequest;

import java.time.LocalDate;

/**
 * Test data factory for building request DTOs and sample domain objects.
 * Keeps tests concise and consistent across layers.
 */
public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static TodoCreateRequest sampleCreateRequest() {
        return new TodoCreateRequest()
                .setTitle("Write tests")
                .setDescription("Add coverage for service and controller")
                .setDueDate(LocalDate.now().plusDays(3));
    }

    public static TodoCreateRequest createRequestWithTitle(String title) {
        return new TodoCreateRequest()
                .setTitle(title)
                .setDescription("desc")
                .setDueDate(LocalDate.now().plusDays(1));
    }

    public static TodoUpdateRequest samplePutUpdateRequest() {
        return new TodoUpdateRequest()
                .setTitle("Updated title")
                .setDescription("Updated description")
                .setCompleted(Boolean.TRUE)
                .setDueDate(LocalDate.now().plusDays(5));
    }

    public static TodoUpdateRequest samplePatchUpdateRequest() {
        // For PATCH keep partial (no title by default)
        return new TodoUpdateRequest()
                .setDescription("Patched description")
                .setCompleted(Boolean.FALSE);
    }

    public static Todo sampleEntity(Long id) {
        // Minimal entity priming for unit-level mapping assertions
        return new Todo()
                .setId(id)
                .setTitle("Existing")
                .setDescription("Existing desc")
                .setCompleted(false)
                .setDueDate(LocalDate.now().plusDays(2));
    }
}
