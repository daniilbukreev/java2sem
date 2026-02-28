package com.mipt.daniilbukreev.repository;

import com.mipt.daniilbukreev.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Interface for data access operations on {@link Task} entities.
 * Defines the standard CRUD operations.
 */
public interface TaskRepository {
    /**
     * Retrieves all tasks.
     * @return a list of all tasks.
     */
    List<Task> findAll();

    /**
     * Retrieves a task by its ID.
     * @param id the ID of the task to retrieve.
     * @return an {@link Optional} containing the task if found, or empty if not.
     */
    Optional<Task> findById(Long id);

    /**
     * Saves a new task or updates an existing one.
     * @param task the task to save.
     * @return the saved task.
     */
    Task save(Task task);

    /**
     * Updates an existing task.
     * @param task the task to update.
     * @return the updated task.
     */
    Task update(Task task);

    /**
     * Deletes a task by its ID.
     * @param id the ID of the task to delete.
     */
    void deleteById(Long id);
}
