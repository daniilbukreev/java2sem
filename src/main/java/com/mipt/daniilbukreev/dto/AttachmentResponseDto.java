package com.mipt.daniilbukreev.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "DTO for displaying Attachment information")
public class AttachmentResponseDto {
    @Schema(description = "Unique identifier of the attachment", example = "1")
    private Long id;

    @Schema(description = "Original file name", example = "document.pdf")
    private String fileName;

    @Schema(description = "Path where the file is stored", example = "/uploads/12345-document.pdf")
    private String filePath;

    @Schema(description = "Content type of the file", example = "application/pdf")
    private String contentType;

    @Schema(description = "Size of the file in bytes", example = "102400")
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
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
