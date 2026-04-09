package com.mipt.daniilbukreev.mapper;

import com.mipt.daniilbukreev.dto.TaskCreateDto;
import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.dto.TaskUpdateDto;
import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.model.Task;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    private TaskMapper mapper = Mappers.getMapper(TaskMapper.class);

    @Test
    void toEntity_ShouldMapTaskCreateDtoToTask() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Test Create");
        dto.setDescription("Desc Create");
        dto.setDueDate(LocalDateTime.now().plusDays(1));
        dto.setPriority(Priority.HIGH);
        dto.setTags(Set.of("tag1", "tag2"));

        Task task = mapper.toEntity(dto);

        assertNotNull(task);
        assertNull(task.getId());
        assertEquals(dto.getTitle(), task.getTitle());
        assertEquals(dto.getDescription(), task.getDescription());
        assertEquals(dto.getDueDate(), task.getDueDate());
        assertEquals(dto.getPriority(), task.getPriority());
        assertEquals(dto.getTags(), task.getTags());
        assertNull(task.getCreatedAt());
        assertFalse(task.isCompleted());
    }

    @Test
    void updateEntity_ShouldUpdateExistingTaskFromTaskUpdateDto() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Updated Title");
        dto.setDescription("Updated Desc");
        dto.setCompleted(true);
        dto.setDueDate(LocalDateTime.now().plusDays(5));
        dto.setPriority(Priority.LOW);
        dto.setTags(Set.of("newTag"));

        Task existingTask = new Task("Original Title", "Original Desc", false,
                LocalDateTime.now().plusDays(10), Priority.MEDIUM, new java.util.HashSet<>(Set.of("oldTag")));
        existingTask.setId(1L);
        existingTask.setCreatedAt(LocalDateTime.now());

        mapper.updateEntity(dto, existingTask);

        assertNotNull(existingTask);
        assertEquals(1L, existingTask.getId());
        assertEquals(dto.getTitle(), existingTask.getTitle());
        assertEquals(dto.getDescription(), existingTask.getDescription());
        assertTrue(existingTask.isCompleted());
        assertEquals(dto.getDueDate(), existingTask.getDueDate());
        assertEquals(dto.getPriority(), existingTask.getPriority());
        assertEquals(dto.getTags(), existingTask.getTags());
        assertNotNull(existingTask.getCreatedAt());
    }

    @Test
    void updateEntity_ShouldHandleNullFieldsInDtoGracefully() {
        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("Partial Update");

        LocalDateTime originalDueDate = LocalDateTime.now().plusDays(10);
        Task existingTask = new Task("Original Title", "Original Desc", false,
                originalDueDate, Priority.MEDIUM, new java.util.HashSet<>(Set.of("oldTag")));
        existingTask.setId(1L);
        existingTask.setCreatedAt(LocalDateTime.now());

        mapper.updateEntity(dto, existingTask);

        assertNotNull(existingTask);
        assertEquals(1L, existingTask.getId());
        assertEquals(dto.getTitle(), existingTask.getTitle());
        assertEquals("Original Desc", existingTask.getDescription());
        assertFalse(existingTask.isCompleted());
        assertEquals(originalDueDate, existingTask.getDueDate());
        assertEquals(Priority.MEDIUM, existingTask.getPriority());
        assertEquals(Set.of("oldTag"), existingTask.getTags());
    }


    @Test
    void toResponseDto_ShouldMapTaskToTaskResponseDto() {
        Task task = new Task("Response Task", "Response Desc", true,
                LocalDateTime.now().plusDays(2), Priority.LOW, Set.of("respTag"));
        task.setId(1L);
        task.setCreatedAt(LocalDateTime.now());

        TaskResponseDto dto = mapper.toResponseDto(task);

        assertNotNull(dto);
        assertEquals(task.getId(), dto.getId());
        assertEquals(task.getTitle(), dto.getTitle());
        assertEquals(task.getDescription(), dto.getDescription());
        assertEquals(task.isCompleted(), dto.isCompleted());
        assertEquals(task.getCreatedAt(), dto.getCreatedAt());
        assertEquals(task.getDueDate(), dto.getDueDate());
        assertEquals(task.getPriority(), dto.getPriority());
        assertEquals(task.getTags(), dto.getTags());
    }
}
