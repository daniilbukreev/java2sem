package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.exception.TaskNotFoundException;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.repository.TaskRepository;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for managing tasks.
 * It encapsulates the business logic for task operations.
 */
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    /**
     * Constructs a TaskService with the given TaskRepository.
     * @param taskRepository the repository to be used for task operations.
     */
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getAllTasksWithAttachments() {
        return taskRepository.findAllWithAttachments();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task getTaskByIdOrThrow(Long id) {
        return getTaskById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        getTaskByIdOrThrow(task.getId());
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        getTaskByIdOrThrow(id);
        taskRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = TaskNotFoundException.class)
    public void bulkCompleteTasks(List<Long> ids) {
        for (Long id : ids) {
            Task task = getTaskByIdOrThrow(id);
            task.setCompleted(true);
            taskRepository.save(task);
        }
    }
}
