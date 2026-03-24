package com.mipt.daniilbukreev.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "DTO for displaying Task Attachment information")
public class AttachmentResponseDto {
    @Schema(description = "Unique identifier of the attachment", example = "1")
    private Long id;
    @Schema(description = "Original name of the attached file", example = "document.pdf")
    private String fileName;
    @Schema(description = "Size of the attached file in bytes", example = "102400")
    private long size;
    @Schema(description = "Date and time when the attachment was uploaded", example = "2026-03-24T10:00:00")
    private LocalDateTime uploadedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
