package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.exception.TaskNotFoundException;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.model.TaskAttachment;
import com.mipt.daniilbukreev.repository.TaskAttachmentRepository;
import com.mipt.daniilbukreev.model.Priority;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceTest {

    @Mock
    private TaskAttachmentRepository attachmentRepository;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private AttachmentService attachmentService;

    private String uploadDir = "./target/uploads-test-service/";
    private Path uploadPath;

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(attachmentService, "uploadDir", uploadDir);
        uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                 .sorted(java.util.Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(java.io.File::delete);
        }
    }

    @Test
    void storeAttachment_ShouldSaveFileAndMetadata() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes());
        Task mockTask = new Task("Test Task", "Test Description", false, LocalDateTime.now(), Priority.HIGH, Collections.emptySet());
        when(taskService.getTaskByIdOrThrow(1L)).thenReturn(mockTask);
        when(attachmentRepository.save(any(TaskAttachment.class))).thenAnswer(invocation -> {
            TaskAttachment attachment = invocation.getArgument(0);
            attachment.setId(1L);
            return attachment;
        });

        TaskAttachment result = attachmentService.storeAttachment(1L, file);

        assertNotNull(result);
        assertEquals("test.txt", result.getFileName());
        assertNotNull(result.getFilePath());
        assertTrue(Files.exists(Paths.get(uploadDir).resolve(result.getFilePath())));
        verify(taskService, times(1)).getTaskByIdOrThrow(1L);
        verify(attachmentRepository, times(1)).save(any(TaskAttachment.class));
    }

    @Test
    void storeAttachment_WhenTaskNotFound_ShouldThrowException() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes());
        when(taskService.getTaskByIdOrThrow(anyLong())).thenThrow(new TaskNotFoundException(99L));

        assertThrows(TaskNotFoundException.class, () -> attachmentService.storeAttachment(99L, file));
        verify(attachmentRepository, never()).save(any(TaskAttachment.class));
    }

    @Test
    void loadAsResource_ShouldReturnResource() throws IOException {
        String fileContent = "Test content for resource";
        Path tempFile = Paths.get(uploadDir).resolve("stored_file.txt");
        Files.write(tempFile, fileContent.getBytes());

        TaskAttachment attachment = new TaskAttachment("stored_file.txt", "stored_file.txt", "text/plain", 100L);
        attachment.setId(1L);
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));

        Resource resource = attachmentService.loadAsResource(1L);

        assertNotNull(resource);
        assertTrue(resource.exists());
        assertEquals(fileContent, new String(resource.getInputStream().readAllBytes()));
        verify(attachmentRepository, times(1)).findById(1L);
    }

    @Test
    void loadAsResource_WhenAttachmentNotFound_ShouldThrowException() {
        when(attachmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> attachmentService.loadAsResource(99L));
    }

    @Test
    void deleteAttachment_ShouldDeleteFileAndMetadata() throws IOException {
        String filePath = "file_to_delete.txt";
        Path tempFile = Paths.get(uploadDir).resolve(filePath);
        Files.write(tempFile, "Content".getBytes());

        TaskAttachment attachment = new TaskAttachment(filePath, filePath, "text/plain", 100L);
        attachment.setId(1L);
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        doNothing().when(attachmentRepository).deleteById(1L);

        attachmentService.deleteAttachment(1L);

        assertFalse(Files.exists(tempFile));
    }

    @Test
    void deleteAttachment_WhenAttachmentNotFound_ShouldThrowException() {
        when(attachmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> attachmentService.deleteAttachment(99L));
        verify(attachmentRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAttachmentsByTaskId_ShouldReturnListOfAttachments() {
        Task mockTask = new Task("Test Task", "Test Description", false, LocalDateTime.now(), Priority.HIGH, Collections.emptySet());
        TaskAttachment attachment = new TaskAttachment("file.txt", "stored.txt", "text/plain", 100L);
        when(taskService.getTaskByIdOrThrow(1L)).thenReturn(mockTask);
        when(attachmentRepository.findByTaskId(1L)).thenReturn(Collections.singletonList(attachment));

        List<TaskAttachment> attachments = attachmentService.getAttachmentsByTaskId(1L);

        assertNotNull(attachments);
        assertEquals(1, attachments.size());
        assertEquals(attachment.getFileName(), attachments.get(0).getFileName());
        verify(taskService, times(1)).getTaskByIdOrThrow(1L);
        verify(attachmentRepository, times(1)).findByTaskId(1L);
    }
}
