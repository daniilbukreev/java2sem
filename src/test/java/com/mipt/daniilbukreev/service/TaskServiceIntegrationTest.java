package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.exception.TaskNotFoundException;
import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskRepository taskRepository;

    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        task1 = taskRepository.save(new Task("Task 1", "Desc 1", false, LocalDateTime.now(), Priority.HIGH, Set.of("tag1")));
        task2 = taskRepository.save(new Task("Task 2", "Desc 2", false, LocalDateTime.now(), Priority.MEDIUM, Set.of("tag2")));
    }

    @Test
    void bulkCompleteTasks_shouldCompleteAllTasks() {
        List<Long> idsToComplete = List.of(task1.getId(), task2.getId());

        taskService.bulkCompleteTasks(idsToComplete);

        Task completedTask1 = taskRepository.findById(task1.getId()).orElseThrow();
        Task completedTask2 = taskRepository.findById(task2.getId()).orElseThrow();

        assertThat(completedTask1.isCompleted()).isTrue();
        assertThat(completedTask2.isCompleted()).isTrue();
    }

    @Test
    void bulkCompleteTasks_whenOneIdNotFound_shouldThrowExceptionAndRollback() {
        long nonExistentId = 999L;
        List<Long> idsToComplete = List.of(task1.getId(), nonExistentId);

        assertThrows(TaskNotFoundException.class, () -> {
            taskService.bulkCompleteTasks(idsToComplete);
        });

        Task rolledBackTask1 = taskRepository.findById(task1.getId()).orElseThrow();
        assertThat(rolledBackTask1.isCompleted()).isFalse();
    }
}
