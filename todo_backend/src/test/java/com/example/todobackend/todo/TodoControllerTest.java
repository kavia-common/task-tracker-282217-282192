package com.example.todobackend.todo;

import com.example.todobackend.exception.GlobalExceptionHandler;
import com.example.todobackend.todo.dto.TodoCreateRequest;
import com.example.todobackend.todo.dto.TodoResponse;
import com.example.todobackend.todo.dto.TodoUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static com.example.todobackend.todo.TestDataFactory.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice test using MockMvc for /api/todos endpoints.
 */
@WebMvcTest(controllers = TodoController.class)
@Import({GlobalExceptionHandler.class})
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Todo buildPersisted(Long id, String title) {
        // Default helper when specific description/dueDate aren't relevant
        return buildPersisted(id, title, "desc", LocalDate.now().plusDays(1));
    }

    private Todo buildPersisted(Long id, String title, String description, LocalDate dueDate) {
        return new Todo()
                .setId(id)
                .setTitle(title)
                .setDescription(description)
                .setCompleted(false)
                .setDueDate(dueDate)
                .setCreatedAt(Instant.now())
                .setUpdatedAt(Instant.now());
    }

    @Test
    void create_success_shouldReturn201AndBody() throws Exception {
        TodoCreateRequest req = sampleCreateRequest();
        Todo saved = buildPersisted(1L, req.getTitle(), req.getDescription(), req.getDueDate());
        when(todoService.create(any(TodoCreateRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.description", is(req.getDescription())))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.dueDate", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void create_validationError_shouldReturn400WithErrors() throws Exception {
        TodoCreateRequest invalid = new TodoCreateRequest()
                .setTitle("   ") // NotBlank violation
                .setDescription("d");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors", not(empty())));
    }

    @Test
    void list_success_shouldReturn200AndArray() throws Exception {
        Todo t1 = buildPersisted(1L, "A");
        Todo t2 = buildPersisted(2L, "B");
        when(todoService.list(null, null)).thenReturn(List.of(t1, t2));

        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
    }

    @Test
    void getById_notFound_shouldReturn404() throws Exception {
        when(todoService.getById(99L)).thenThrow(new com.example.todobackend.exception.NotFoundException("not found"));

        mockMvc.perform(get("/api/todos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsString("not found")));
    }

    @Test
    void put_success_shouldReturnUpdatedResponse() throws Exception {
        TodoUpdateRequest req = samplePutUpdateRequest();
        Todo updated = buildPersisted(1L, req.getTitle()).setDescription(req.getDescription()).setCompleted(true).setDueDate(req.getDueDate());
        when(todoService.update(eq(1L), any(TodoUpdateRequest.class), eq(true))).thenReturn(updated);

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(req.getTitle())))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void put_missingTitle_shouldMapIllegalArgumentTo400() throws Exception {
        TodoUpdateRequest req = new TodoUpdateRequest().setDescription("desc");
        when(todoService.update(eq(1L), any(TodoUpdateRequest.class), eq(true)))
                .thenThrow(new IllegalArgumentException("title is required for PUT"));

        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("title is required for PUT")));
    }

    @Test
    void patch_success_shouldReturnUpdatedResponse() throws Exception {
        TodoUpdateRequest req = samplePatchUpdateRequest();
        Todo updated = buildPersisted(3L, "Existing").setDescription(req.getDescription()).setCompleted(false);
        when(todoService.update(eq(3L), any(TodoUpdateRequest.class), eq(false))).thenReturn(updated);

        mockMvc.perform(patch("/api/todos/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.description", is(req.getDescription())))
                .andExpect(jsonPath("$.completed", is(false)));
    }

    @Test
    void toggleComplete_shouldReturnUpdatedState() throws Exception {
        Todo toggled = buildPersisted(5L, "X").setCompleted(true);
        when(todoService.toggleComplete(5L)).thenReturn(toggled);

        mockMvc.perform(patch("/api/todos/5/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.completed", is(true)));
    }

    @Test
    void delete_success_shouldReturn204() throws Exception {
        Mockito.doNothing().when(todoService).delete(7L);

        mockMvc.perform(delete("/api/todos/7"))
                .andExpect(status().isNoContent());
    }
}
