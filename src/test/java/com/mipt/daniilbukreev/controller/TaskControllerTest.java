package com.mipt.daniilbukreev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.daniilbukreev.dto.TaskCreateDto;
import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.dto.TaskUpdateDto;
import com.mipt.daniilbukreev.mapper.TaskMapper;
import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.service.TaskService;
import com.mipt.daniilbukreev.validation.OnCreate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "app.upload-dir=./target/uploads-test")
public class TaskControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private Task createTask(Long id, String title) {
        return new Task(id, title, "Desc", false, LocalDateTime.now(), LocalDate.now().plusDays(1), Priority.MEDIUM, Set.of("test"));
    }

    private TaskResponseDto createTaskResponseDto(Long id, String title) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        List<Task> tasks = List.of(createTask(1L, "Task 1"));
        given(taskService.getAllTasks()).willReturn(tasks);
        given(taskMapper.toResponseDto(any(Task.class))).willReturn(createTaskResponseDto(1L, "Task 1"));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Task 1")));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTask() throws Exception {
        Task task = createTask(1L, "Test Task");
        given(taskService.getTaskById(1L)).willReturn(Optional.of(task));
        given(taskMapper.toResponseDto(task)).willReturn(createTaskResponseDto(1L, "Test Task"));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        given(taskService.getTaskById(99L)).willReturn(Optional.empty());
        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTask_WithValidData_ShouldReturnCreatedTask() throws Exception {
        TaskCreateDto taskDto = new TaskCreateDto();
        taskDto.setTitle("New Task");
        taskDto.setPriority(Priority.HIGH);
        taskDto.setDueDate(LocalDate.now().plusDays(1));

        Task createdTask = createTask(1L, "New Task");
        TaskResponseDto responseDto = createTaskResponseDto(1L, "New Task");

        given(taskMapper.toEntity(any(TaskCreateDto.class))).willReturn(new Task());
        given(taskService.createTask(any(Task.class))).willReturn(createdTask);
        given(taskMapper.toResponseDto(createdTask)).willReturn(responseDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void createTask_WithInvalidTitle_ShouldReturnBadRequest() throws Exception {
        TaskCreateDto taskDto = new TaskCreateDto();
        taskDto.setTitle("T");
        taskDto.setPriority(Priority.HIGH);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                        .requestAttr("validationGroups", OnCreate.class))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_WithPastDueDate_ShouldReturnBadRequest() throws Exception {
        TaskCreateDto taskDto = new TaskCreateDto();
        taskDto.setTitle("A valid title");
        taskDto.setPriority(Priority.HIGH);
        taskDto.setDueDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))
                        .requestAttr("validationGroups", OnCreate.class))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_WithValidData_ShouldReturnUpdatedTask() throws Exception {
        TaskUpdateDto taskDto = new TaskUpdateDto();
        taskDto.setTitle("Updated Title");

        Task existingTask = createTask(1L, "Old Title");
        Task updatedTask = createTask(1L, "Updated Title");
        TaskResponseDto responseDto = createTaskResponseDto(1L, "Updated Title");

        given(taskService.getTaskByIdOrThrow(1L)).willReturn(existingTask);
        given(taskMapper.updateEntity(any(TaskUpdateDto.class), any(Task.class))).willReturn(updatedTask);
        given(taskService.updateTask(any(Task.class))).willReturn(updatedTask);
        given(taskMapper.toResponseDto(updatedTask)).willReturn(responseDto);
        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")));
    }
    
    @Test
    void deleteTask_WhenTaskExists_ShouldReturnNoContent() throws Exception {
        given(taskService.getTaskByIdOrThrow(1L)).willReturn(createTask(1L, "Task to delete"));
        doNothing().when(taskService).deleteTask(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}
