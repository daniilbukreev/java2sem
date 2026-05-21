package com.mipt.daniilbukreev.repository;

import com.mipt.daniilbukreev.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldFindTasksDueWithin7Days() {
        LocalDateTime now = LocalDateTime.now();

        Task taskDueIn3Days = new Task();
        taskDueIn3Days.setTitle("Due in 3 days");
        taskDueIn3Days.setDueDate(now.plusDays(3));
        taskRepository.save(taskDueIn3Days);

        Task taskDueIn10Days = new Task();
        taskDueIn10Days.setTitle("Due in 10 days");
        taskDueIn10Days.setDueDate(now.plusDays(10));
        taskRepository.save(taskDueIn10Days);

        List<Task> foundTasks = taskRepository.findTasksDueWithin7Days(now, now.plusDays(7));

        assertThat(foundTasks).hasSize(1);
        assertThat(foundTasks.get(0).getTitle()).isEqualTo("Due in 3 days");
    }
}
