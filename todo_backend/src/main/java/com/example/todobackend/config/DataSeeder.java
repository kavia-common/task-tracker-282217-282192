package com.example.todobackend.config;

import com.example.todobackend.todo.Todo;
import com.example.todobackend.todo.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Configuration providing a dev-only data seeder. Seeds a few Todos when repository is empty.
 * This runs only when the 'dev' Spring profile is active.
 */
@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    /**
     * PUBLIC_INTERFACE
     * CommandLineRunner seeding sample Todos if the repository is empty.
     *
     * The seeding will:
     * - Insert two to three example todos with mixed completion state
     * - Only execute when the active Spring profile includes 'dev'
     */
    @Bean
    public CommandLineRunner seedTodos(TodoRepository repository) {
        return args -> {
            long count = repository.count();
            if (count > 0) {
                log.info("Skipping seeding: repository already contains {} todos.", count);
                return;
            }

            Todo t1 = new Todo()
                    .setTitle("Write docs")
                    .setDescription("Draft initial API documentation and README")
                    .setDueDate(LocalDate.now().plusDays(3))
                    .setCompleted(false);

            Todo t2 = new Todo()
                    .setTitle("Review PRs")
                    .setDescription("Review pending pull requests and provide feedback")
                    .setDueDate(LocalDate.now().plusDays(1))
                    .setCompleted(true);

            Todo t3 = new Todo()
                    .setTitle("Plan next sprint")
                    .setDescription("Prepare backlog and priorities for next sprint")
                    .setDueDate(LocalDate.now().plusDays(7))
                    .setCompleted(false);

            List<Todo> toSave = List.of(t1, t2, t3);
            repository.saveAll(toSave);
            log.info("Seeded {} todos for dev profile.", toSave.size());
        };
    }
}
