package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.exception.TaskNotFoundException;
import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task("Test Task", "Description", false,
                LocalDateTime.now().plusDays(7), Priority.MEDIUM, Set.of("tag1"));
        sampleTask.setId(1L);
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void getAllTasks_ShouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(sampleTask, new Task("Another Task", "Desc", true, LocalDateTime.now().plusDays(1), Priority.LOW, Set.of())));

        List<Task> tasks = taskService.getAllTasks();

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Optional<Task> foundTask = taskService.getTaskById(1L);

        assertTrue(foundTask.isPresent());
        assertEquals(sampleTask.getTitle(), foundTask.get().getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnEmptyOptional() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Task> foundTask = taskService.getTaskById(99L);

        assertFalse(foundTask.isPresent());
        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    void getTaskByIdOrThrow_WhenTaskExists_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        Task foundTask = taskService.getTaskByIdOrThrow(1L);

        assertNotNull(foundTask);
        assertEquals(sampleTask.getTitle(), foundTask.getTitle());
        verify(taskRepository, times(1)).findById(1L);
    }

    @Test
    void getTaskByIdOrThrow_WhenTaskDoesNotExist_ShouldThrowException() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskByIdOrThrow(99L));
        verify(taskRepository, times(1)).findById(99L);
    }

    @Test
    void createTask_ShouldSetCreatedAtAndSaveTask() {
        Task newTask = new Task("New Task", "New Desc", false, LocalDateTime.now().plusDays(5), Priority.HIGH, Set.of("new"));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            task.setId(3L);
            task.setCreatedAt(LocalDateTime.now());
            task.setUpdatedAt(LocalDateTime.now());
            return task;
        });

        Task createdTask = taskService.createTask(newTask);

        assertNotNull(createdTask.getId());
        assertNotNull(createdTask.getCreatedAt());
        assertEquals("New Task", createdTask.getTitle());
        verify(taskRepository, times(1)).save(newTask);
    }

    @Test
    void updateTask_ShouldSaveTask() {
        Task updatedTask = new Task("Updated Task", "Updated Desc", true,
                LocalDateTime.now().plusDays(10), Priority.LOW, Set.of("updated"));
        updatedTask.setId(1L);
        updatedTask.setCreatedAt(sampleTask.getCreatedAt());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        System.out.println("Passed task to service: " + updatedTask);
        Task result = taskService.updateTask(updatedTask);

        assertNotNull(result);
        assertEquals(updatedTask.getTitle(), result.getTitle());
        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).save(updatedTask);
    }

    @Test
    void deleteTask_ShouldCallRepositoryDelete() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).deleteById(1L);

        taskService.deleteTask(1L);

        verify(taskRepository, times(1)).findById(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }
}
