package com.example.todolist.repository;

import com.example.todolist.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * A stub implementation of the {@link TaskRepository} that returns fixed data.
 */
public class StubTaskRepository implements TaskRepository {

    private final List<Task> tasks = List.of(
            new Task(1L, "Stub Task 1", "This is a stub task.", true),
            new Task(2L, "Stub Task 2", "This is another stub task.", false)
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
