package com.example.todobackend.todo;

import com.example.todobackend.exception.NotFoundException;
import com.example.todobackend.todo.dto.TodoCreateRequest;
import com.example.todobackend.todo.dto.TodoUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.todobackend.todo.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TodoServiceTest {

    private TodoRepository repository;
    private TodoService service;

    @BeforeEach
    void setUp() {
        repository = mock(TodoRepository.class);
        service = new TodoService(repository);
    }

    @Test
    void create_shouldPersistWithDefaults() {
        TodoCreateRequest req = sampleCreateRequest();
        ArgumentCaptor<Todo> captor = ArgumentCaptor.forClass(Todo.class);
        Todo saved = new Todo().setId(10L).setTitle(req.getTitle()).setDescription(req.getDescription()).setDueDate(req.getDueDate());
        when(repository.save(any(Todo.class))).thenReturn(saved);

        Todo result = service.create(req);

        verify(repository).save(captor.capture());
        Todo toSave = captor.getValue();
        assertThat(toSave.getId()).isNull();
        assertThat(toSave.getTitle()).isEqualTo(req.getTitle());
        assertThat(toSave.getDescription()).isEqualTo(req.getDescription());
        assertThat(toSave.getDueDate()).isEqualTo(req.getDueDate());
        assertThat(toSave.isCompleted()).isFalse();

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void list_shouldDelegateToRepositoryWithNormalizedQuery() {
        when(repository.findFiltered(null, null)).thenReturn(List.of());
        List<Todo> res1 = service.list(null, "   ");
        verify(repository).findFiltered(null, null);
        assertThat(res1).isEmpty();

        reset(repository);
        when(repository.findFiltered(Boolean.TRUE, "abc")).thenReturn(List.of(sampleEntity(1L)));
        List<Todo> res2 = service.list(Boolean.TRUE, " abc ");
        verify(repository).findFiltered(Boolean.TRUE, "abc");
        assertThat(res2).hasSize(1);
    }

    @Test
    void getById_whenMissing_shouldThrowNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void update_put_requiresTitleAndOverwritesAll() {
        Todo existing = new Todo().setId(1L).setTitle("Old").setDescription("old").setCompleted(false).setDueDate(LocalDate.now().plusDays(1));
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        TodoUpdateRequest req = samplePutUpdateRequest();
        Todo updated = service.update(1L, req, true);

        assertThat(updated.getTitle()).isEqualTo(req.getTitle());
        assertThat(updated.getDescription()).isEqualTo(req.getDescription());
        assertThat(updated.getDueDate()).isEqualTo(req.getDueDate());
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void update_put_missingTitle_shouldThrowIllegalArgument() {
        Todo existing = new Todo().setId(1L).setTitle("Old");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));

        TodoUpdateRequest req = new TodoUpdateRequest()
                .setDescription("desc"); // no title

        assertThatThrownBy(() -> service.update(1L, req, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("title is required for PUT");
    }

    @Test
    void update_patch_shouldOnlyApplyProvidedFields() {
        Todo existing = new Todo().setId(1L).setTitle("Old").setDescription("old").setCompleted(true).setDueDate(LocalDate.now().plusDays(2));
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        TodoUpdateRequest req = samplePatchUpdateRequest(); // description + completed(false)
        Todo updated = service.update(1L, req, false);

        assertThat(updated.getTitle()).isEqualTo("Old");
        assertThat(updated.getDescription()).isEqualTo("Patched description");
        assertThat(updated.isCompleted()).isFalse();
    }

    @Test
    void toggleComplete_shouldFlipAndPersist() {
        Todo existing = new Todo().setId(5L).setTitle("t").setCompleted(false);
        when(repository.findById(5L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Todo.class))).thenAnswer(inv -> inv.getArgument(0));

        Todo toggled = service.toggleComplete(5L);
        assertThat(toggled.isCompleted()).isTrue();

        // Flip again
        when(repository.findById(5L)).thenReturn(Optional.of(toggled));
        toggled = service.toggleComplete(5L);
        assertThat(toggled.isCompleted()).isFalse();
    }

    @Test
    void delete_missing_shouldThrowNotFound() {
        when(repository.existsById(100L)).thenReturn(false);
        assertThatThrownBy(() -> service.delete(100L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("100");
    }

    @Test
    void delete_existing_shouldDelete() {
        when(repository.existsById(1L)).thenReturn(true);
        service.delete(1L);
        verify(repository).deleteById(1L);
    }
}
