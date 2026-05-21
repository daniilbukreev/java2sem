package com.mipt.daniilbukreev.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mipt.daniilbukreev.dto.TaskCreateDto;
import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.mapper.TaskMapper;
import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    void createTask_ShouldReturnCreated() throws Exception {
        TaskCreateDto createDto = new TaskCreateDto();
        createDto.setTitle("New Task");
        createDto.setPriority(Priority.HIGH);
        createDto.setDueDate(LocalDateTime.now().plusDays(1));

        Task task = new Task();
        task.setId(1L);
        task.setTitle("New Task");

        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("New Task");

        given(taskMapper.toEntity(any(TaskCreateDto.class))).willReturn(task);
        given(taskService.createTask(any(Task.class))).willReturn(task);
        given(taskMapper.toResponseDto(any(Task.class))).willReturn(responseDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/tasks/1"))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("New Task")));
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");

        TaskResponseDto responseDto = new TaskResponseDto();
        responseDto.setId(taskId);
        responseDto.setTitle("Test Task");

        given(taskService.getTaskById(taskId)).willReturn(Optional.of(task));
        given(taskMapper.toResponseDto(any(Task.class))).willReturn(responseDto);

        mockMvc.perform(get("/api/tasks/{id}", taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")));
    }
}
