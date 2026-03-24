package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.AttachmentResponseDto;
import com.mipt.daniilbukreev.mapper.AttachmentMapper;
import com.mipt.daniilbukreev.service.AttachmentService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;

    public AttachmentController(AttachmentService attachmentService, AttachmentMapper attachmentMapper) {
        this.attachmentService = attachmentService;
        this.attachmentMapper = attachmentMapper;
    }

    @PostMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<AttachmentResponseDto> uploadFile(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        AttachmentResponseDto dto = attachmentMapper.toResponseDto(attachmentService.storeAttachment(taskId, file));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/attachments/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) {
        Resource resource = attachmentService.loadAsResource(attachmentId);
        String contentType = "application/octet-stream";
        try {
            contentType = resource.getURL().openConnection().guessContentTypeFromName(resource.getFilename());
        } catch (Exception e) {
            System.err.println("Could not determine file type: " + e.getMessage());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> getAttachmentsForTask(@PathVariable Long taskId) {
        List<AttachmentResponseDto> dtos = attachmentService.getAttachmentsByTaskId(taskId).stream()
                .map(attachmentMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
