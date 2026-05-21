package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    @Test
    void shouldUpdateTaskStatus() {
        long taskId = 1L;
        Task existingTask = new Task();
        existingTask.setId(taskId);
        existingTask.setCompleted(false);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));

        Task updatedTask = new Task();
        updatedTask.setId(taskId);
        updatedTask.setCompleted(true);

        taskService.updateTask(updatedTask);

        verify(taskRepository).save(taskArgumentCaptor.capture());
        Task savedTask = taskArgumentCaptor.getValue();
        assertTrue(savedTask.isCompleted());
    }
}
