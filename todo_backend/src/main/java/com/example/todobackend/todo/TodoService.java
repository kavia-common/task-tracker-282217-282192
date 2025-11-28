package com.example.todobackend.todo;

import com.example.todobackend.exception.NotFoundException;
import com.example.todobackend.todo.dto.TodoCreateRequest;
import com.example.todobackend.todo.dto.TodoUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Service for managing Todo items.
 */
@Service
@Transactional
public class TodoService {

    private final TodoRepository repository;

    public TodoService(TodoRepository repository) {
        this.repository = repository;
    }

    // PUBLIC_INTERFACE
    public Todo create(TodoCreateRequest req) {
        Todo t = new Todo()
                .setTitle(req.getTitle())
                .setDescription(req.getDescription())
                .setDueDate(req.getDueDate())
                .setCompleted(false);
        return repository.save(t);
    }

    // PUBLIC_INTERFACE
    @Transactional(readOnly = true)
    public List<Todo> list(Boolean completed, String q) {
        return repository.findFiltered(completed, (q == null || q.isBlank()) ? null : q.trim());
    }

    // PUBLIC_INTERFACE
    @Transactional(readOnly = true)
    public Todo getById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NotFoundException("Todo with id " + id + " not found"));
    }

    // PUBLIC_INTERFACE
    public Todo update(Long id, TodoUpdateRequest req, boolean isPut) {
        Todo t = getById(id);

        if (isPut) {
            // For PUT, title is required
            if (req.getTitle() == null || req.getTitle().isBlank()) {
                throw new IllegalArgumentException("title is required for PUT");
            }
            t.setTitle(req.getTitle());
            t.setDescription(req.getDescription());
            t.setDueDate(req.getDueDate());
            if (req.getCompleted() != null) {
                t.setCompleted(req.getCompleted());
            }
        } else {
            // PATCH - only set fields that are present (non-null)
            if (req.getTitle() != null) {
                t.setTitle(req.getTitle());
            }
            if (req.getDescription() != null) {
                t.setDescription(req.getDescription());
            }
            if (req.getDueDate() != null) {
                t.setDueDate(req.getDueDate());
            }
            if (req.getCompleted() != null) {
                t.setCompleted(req.getCompleted());
            }
        }

        return repository.save(t);
    }

    // PUBLIC_INTERFACE
    public Todo toggleComplete(Long id) {
        Todo t = getById(id);
        t.setCompleted(!t.isCompleted());
        return repository.save(t);
    }

    // PUBLIC_INTERFACE
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Todo with id " + id + " not found");
        }
        repository.deleteById(id);
    }
}
