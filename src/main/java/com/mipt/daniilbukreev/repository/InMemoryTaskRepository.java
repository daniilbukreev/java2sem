package com.example.todolist.repository;

import com.example.todolist.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of the {@link TaskRepository}.
 * This is the primary repository used for storing tasks.
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    @Override
    public List<Task> findAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(tasks.get(id));
    }

    @Override
    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(counter.incrementAndGet());
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task update(Task task) {
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void deleteById(Long id) {
        tasks.remove(id);
    }
}
