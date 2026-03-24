package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.AttachmentResponseDto;
import com.mipt.daniilbukreev.exception.TaskNotFoundException;
import com.mipt.daniilbukreev.mapper.AttachmentMapper;
import com.mipt.daniilbukreev.model.TaskAttachment;
import com.mipt.daniilbukreev.service.AttachmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttachmentController.class)
public class AttachmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AttachmentService attachmentService;

    @MockBean
    private AttachmentMapper attachmentMapper;

    @Test
    void uploadFile_ShouldReturnAttachmentMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Hello, World!".getBytes()
        );

        TaskAttachment attachment = new TaskAttachment(1L, 1L, "test.txt", "uuid_test.txt", "text/plain", 13L, LocalDateTime.now());
        AttachmentResponseDto responseDto = new AttachmentResponseDto();
        responseDto.setId(1L);
        responseDto.setFileName("test.txt");

        given(attachmentService.storeAttachment(anyLong(), any())).willReturn(attachment);
        given(attachmentMapper.toResponseDto(any())).willReturn(responseDto);

        mockMvc.perform(multipart("/api/tasks/1/attachments").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fileName").value("test.txt"));
    }

    @Test
    void downloadFile_ShouldReturnFileResource() throws Exception {
        Resource resource = new ByteArrayResource("file content".getBytes());
        given(attachmentService.loadAsResource(1L)).willReturn(resource);

        mockMvc.perform(get("/api/attachments/1"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"null\""))
                .andExpect(content().string("file content"));
    }

    @Test
    void deleteFile_ShouldReturnNoContent() throws Exception {
        doNothing().when(attachmentService).deleteAttachment(1L);

        mockMvc.perform(delete("/api/attachments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAttachmentsForTask_ShouldReturnListOfAttachments() throws Exception {
        TaskAttachment attachment = new TaskAttachment(1L, 1L, "test.txt", "uuid_test.txt", "text/plain", 13L, LocalDateTime.now());
        AttachmentResponseDto responseDto = new AttachmentResponseDto();
        responseDto.setId(1L);
        responseDto.setFileName("test.txt");

        given(attachmentService.getAttachmentsByTaskId(1L)).willReturn(Collections.singletonList(attachment));
        given(attachmentMapper.toResponseDto(any())).willReturn(responseDto);

        mockMvc.perform(get("/api/tasks/1/attachments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].fileName").value("test.txt"));
    }

    @Test
    void uploadFile_WhenTaskNotFound_ShouldReturnNotFound() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", MediaType.TEXT_PLAIN_VALUE, "content".getBytes());
        given(attachmentService.storeAttachment(anyLong(), any())).willThrow(new TaskNotFoundException(99L));

        mockMvc.perform(multipart("/api/tasks/99/attachments").file(file))
                .andExpect(status().isNotFound());
    }
}
