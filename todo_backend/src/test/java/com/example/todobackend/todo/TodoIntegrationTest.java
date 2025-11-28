package com.example.todobackend.todo;

import com.example.todobackend.todo.dto.TodoCreateRequest;
import com.example.todobackend.todo.dto.TodoUpdateRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.LocalDate;

import static com.example.todobackend.todo.TestDataFactory.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end integration tests booting full Spring context and using MockMvc.
 * Uses in-memory H2 (already configured via application.properties).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    int port; // not used directly; MockMvc avoids actual port binding asserts.

    @Test
    void fullFlow_crudAndQueries() throws Exception {
        // 1) Create
        TodoCreateRequest createReq = sampleCreateRequest();
        String createJson = objectMapper.writeValueAsString(createReq);

        String location1 = mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is(createReq.getTitle())))
                .andReturn().getResponse().getContentAsString();

        // 2) Create second todo - completed false
        TodoCreateRequest createReq2 = createRequestWithTitle("Second task");
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq2)))
                .andExpect(status().isCreated());

        // 3) List all
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // 4) Search filter q
        mockMvc.perform(get("/api/todos").param("q", "Second"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", containsString("Second")));

        // 5) Get by id (id=1)
        mockMvc.perform(get("/api/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", notNullValue()))
                .andExpect(jsonPath("$.description", notNullValue()))
                .andExpect(jsonPath("$.completed", is(false)))
                .andExpect(jsonPath("$.dueDate", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));

        // 6) PUT update id=1
        TodoUpdateRequest putReq = samplePutUpdateRequest();
        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(putReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is(putReq.getTitle())))
                .andExpect(jsonPath("$.description", is(putReq.getDescription())))
                .andExpect(jsonPath("$.completed", is(true)));

        // 7) PATCH partial update id=2 (mark completed false -> true) and change description
        TodoUpdateRequest patchReq = new TodoUpdateRequest()
                .setCompleted(true)
                .setDescription("Integration patched");
        mockMvc.perform(patch("/api/todos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.description", is("Integration patched")))
                .andExpect(jsonPath("$.completed", is(true)));

        // 8) Toggle completion on id=1
        mockMvc.perform(patch("/api/todos/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                // Was true from PUT, toggling makes it false
                .andExpect(jsonPath("$.completed", is(false)));

        // 9) Delete id=2
        mockMvc.perform(delete("/api/todos/2"))
                .andExpect(status().isNoContent());

        // 10) Verify only 1 left
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void validationErrors_shouldReturnStructured400() throws Exception {
        // POST with blank title
        TodoCreateRequest invalid = new TodoCreateRequest()
                .setTitle("  ")
                .setDescription("x");

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", is("Validation failed")))
                .andExpect(jsonPath("$.errors", not(empty())));

        // PUT with missing title
        TodoCreateRequest valid = createRequestWithTitle("valid");
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(valid)))
                .andExpect(status().isCreated());

        TodoUpdateRequest invalidPut = new TodoUpdateRequest().setDescription("only desc");
        mockMvc.perform(put("/api/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPut)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("title is required for PUT")));
    }
}
