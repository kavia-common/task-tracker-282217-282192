package com.example.todobackend.todo;

import com.example.todobackend.exception.GlobalExceptionHandler;
import com.example.todobackend.todo.dto.TodoUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Specific controller tests validating @Valid constraints on update requests.
 */
@WebMvcTest(controllers = TodoController.class)
@Import({GlobalExceptionHandler.class})
class TodoControllerValidationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TodoService todoService;

    @Test
    void put_tooLongTitle_shouldReturn400WithValidationErrors() throws Exception {
        String longTitle = "x".repeat(260);
        TodoUpdateRequest req = new TodoUpdateRequest().setTitle(longTitle);

        // We don't hit service due to validation failing in controller layer
        mockMvc.perform(put("/api/todos/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors", notNullValue()));
    }
}
