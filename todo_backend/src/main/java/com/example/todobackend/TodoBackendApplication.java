package com.example.todobackend;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PUBLIC_INTERFACE
 * Application entry point for the Todo backend.
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Todo Backend API",
                version = "1.0.0",
                description = "REST API for managing todos"
        )
)
public class TodoBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoBackendApplication.class, args);
    }
}
