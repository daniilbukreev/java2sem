package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service layer for managing tasks.
 * It encapsulates the business logic for task operations.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final Map<Long, Task> taskCache = new ConcurrentHashMap<>();

    /**
     * Constructs a TaskService with the given TaskRepository.
     * @param taskRepository the repository to be used for task operations.
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /**
     * Initializes the task cache after the bean has been constructed.
     */
    @PostConstruct
    public void initCache() {
        System.out.println("TaskService: Initializing cache...");
        Task task1 = new Task(null, "Initial Task 1", "Loaded from PostConstruct", false);
        Task task2 = new Task(null, "Initial Task 2", "Another PostConstruct task", false);
        taskRepository.save(task1);
        taskRepository.save(task2);
        taskCache.put(task1.getId(), task1);
        taskCache.put(task2.getId(), task2);
        System.out.println("TaskService: Cache initialized with " + taskCache.size() + " tasks");
    }

    /**
     * Cleans up resources before the bean is destroyed.
     * Logs the number of tasks currently in the cache.
     */
    @PreDestroy
    public void cleanup() {
        System.out.println("TaskService: Cleaning up resources... Cache size: " + taskCache.size());
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        if (taskCache.containsKey(id)) {
            System.out.println("Fetching task " + id + " from cache.");
            return Optional.of(taskCache.get(id));
        }
        return taskRepository.findById(id);
    }

    public Task createTask(Task task) {
        Task savedTask = taskRepository.save(task);
        taskCache.put(savedTask.getId(), savedTask);
        return savedTask;
    }

    public Task updateTask(Task task) {
        Task updatedTask = taskRepository.update(task);
        taskCache.put(updatedTask.getId(), updatedTask);
        return updatedTask;
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
        taskCache.remove(id);
    }
}
