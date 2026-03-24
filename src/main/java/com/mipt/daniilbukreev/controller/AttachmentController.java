package com.mipt.daniilbukreev.controller;

import com.mipt.daniilbukreev.dto.AttachmentResponseDto;
import com.mipt.daniilbukreev.dto.ErrorResponse;
import com.mipt.daniilbukreev.mapper.AttachmentMapper;
import com.mipt.daniilbukreev.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
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
@Tag(name = "Attachments", description = "Task attachment management APIs")
public class AttachmentController {

    private final AttachmentService attachmentService;
    private final AttachmentMapper attachmentMapper;

    @Value("${app.version}")
    private String apiVersion;

    public AttachmentController(AttachmentService attachmentService, AttachmentMapper attachmentMapper) {
        this.attachmentService = attachmentService;
        this.attachmentMapper = attachmentMapper;
    }

    @Operation(summary = "Upload an attachment for a task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment uploaded successfully",
                            content = @Content(schema = @Schema(implementation = AttachmentResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid file or task ID",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<AttachmentResponseDto> uploadFile(@PathVariable Long taskId, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().header("X-API-Version", apiVersion).build();
        }
        AttachmentResponseDto dto = attachmentMapper.toResponseDto(attachmentService.storeAttachment(taskId, file));
        return ResponseEntity.ok().header("X-API-Version", apiVersion).body(dto);
    }

    @Operation(summary = "Download an attachment by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attachment downloaded successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)),
                    @ApiResponse(responseCode = "404", description = "Attachment not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
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
                .header("X-API-Version", apiVersion)
                .body(resource);
    }

    @Operation(summary = "Delete an attachment by ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Attachment deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Attachment not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().header("X-API-Version", apiVersion).build();
    }

    @Operation(summary = "Retrieve all attachments for a specific task",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of attachments",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AttachmentResponseDto.class)))),
                    @ApiResponse(responseCode = "404", description = "Task not found",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/tasks/{taskId}/attachments")
    public ResponseEntity<List<AttachmentResponseDto>> getAttachmentsForTask(@PathVariable Long taskId) {
        List<AttachmentResponseDto> dtos = attachmentService.getAttachmentsByTaskId(taskId).stream()
                .map(attachmentMapper::toResponseDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok().header("X-API-Version", apiVersion).body(dtos);
    }
}
