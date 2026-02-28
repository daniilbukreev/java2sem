package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link TaskController}.
 * These tests use a {@link TestRestTemplate} to send real HTTP requests
 * and a mock {@link TaskService} to isolate the controller logic.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private TaskService taskService;


    @Test
    void getAllTasks_Positive_ReturnsListOfTasks() {
        List<Task> tasks = List.of(new Task(1L, "Task 1", "Desc 1", false));
        when(taskService.getAllTasks()).thenReturn(tasks);

        ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].getTitle()).isEqualTo("Task 1");
    }

    @Test
    void getAllTasks_Negative_ReturnsEmptyList() {
        when(taskService.getAllTasks()).thenReturn(Collections.emptyList());

        ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }


    @Test
    void getTaskById_Positive_ReturnsTask() {
        Task task = new Task(1L, "Test Task", "Test Desc", false);
        when(taskService.getTaskById(1L)).thenReturn(Optional.of(task));

        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/1", Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void getTaskById_Negative_NotFound() {
        when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

        ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/99", Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void createTask_Positive_ReturnsCreatedTask() {
        Task taskToCreate = new Task(null, "New Task", "New Desc", false);
        Task createdTask = new Task(1L, "New Task", "New Desc", false);
        when(taskService.createTask(any(Task.class))).thenReturn(createdTask);

        ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", taskToCreate, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void createTask_Negative_ServerError() {
        Task taskToCreate = new Task(null, "New Task", "New Desc", false);
        when(taskService.createTask(any(Task.class))).thenThrow(new IllegalArgumentException("Invalid data"));
        ResponseEntity<Void> response = restTemplate.postForEntity("/api/tasks", taskToCreate, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Test
    void updateTask_Positive_ReturnsUpdatedTask() {
        Task taskToUpdate = new Task(1L, "Updated Task", "Updated Desc", true);
        when(taskService.updateTask(any(Task.class))).thenReturn(taskToUpdate);

        HttpEntity<Task> requestEntity = new HttpEntity<>(taskToUpdate);
        ResponseEntity<Task> response = restTemplate.exchange("/api/tasks/1", HttpMethod.PUT, requestEntity, Task.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Updated Task");
    }

    @Test
    void updateTask_Negative_NotFound() {
        Task taskToUpdate = new Task(99L, "Non-existent Task", "Desc", false);
        when(taskService.updateTask(any(Task.class))).thenThrow(new RuntimeException("Task not found"));

        HttpEntity<Task> requestEntity = new HttpEntity<>(taskToUpdate);
        ResponseEntity<Void> response = restTemplate.exchange("/api/tasks/99", HttpMethod.PUT, requestEntity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @Test
    void deleteTask_Positive_ReturnsNoContent() {
        doNothing().when(taskService).deleteTask(1L);

        ResponseEntity<Void> response = restTemplate.exchange("/api/tasks/1", HttpMethod.DELETE, null, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteTask_Negative_VerifyTaskIsGone() {
        doNothing().when(taskService).deleteTask(1L);
        when(taskService.getTaskById(1L)).thenReturn(Optional.empty());

        restTemplate.delete("/api/tasks/1");

        ResponseEntity<Void> response = restTemplate.getForEntity("/api/tasks/1", Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getScopeBeans_Positive_ReturnsBeanInfo() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks/scopes", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).contains("RequestScopedBean");
        assertThat(response.getBody()).contains("Prototype Bean 1");
        assertThat(response.getBody()).contains("Prototype Bean 2");
    }
}
