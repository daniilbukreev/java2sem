package com.mipt.daniilbukreev.repository;

import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A stub implementation of the {@link TaskRepository} that returns fixed data.
 */
public class StubTaskRepository implements TaskRepository {

    private final List<Task> tasks = List.of(
            new Task(1L, "Stub Task 1", "This is a stub task.", true, LocalDateTime.now(), LocalDate.now().plusDays(1), Priority.HIGH, Set.of("stub")),
            new Task(2L, "Stub Task 2", "This is another stub task.", false, LocalDateTime.now(), LocalDate.now().plusDays(2), Priority.MEDIUM, Set.of("stub", "test"))
    );

    @Override
    public List<Task> findAll() {
        return tasks;
    }

    @Override
    public Optional<Task> findById(Long id) {
        return tasks.stream().filter(task -> task.getId().equals(id)).findFirst();
    }

    @Override
    public Task save(Task task) {
        return task;
    }

    @Override
    public Task update(Task task) {
        return task;
    }

    @Override
    public void deleteById(Long id) {
    }
}
