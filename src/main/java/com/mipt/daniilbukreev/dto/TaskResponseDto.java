package com.mipt.daniilbukreev.dto;

import com.mipt.daniilbukreev.model.Priority;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "DTO for displaying Task information")
public class TaskResponseDto {
    @Schema(description = "Unique identifier of the task", example = "1")
    private Long id;

    @Schema(description = "Title of the task", example = "Buy groceries")
    private String title;

    @Schema(description = "Description of the task", example = "Milk, Eggs, Bread")
    private String description;

    @Schema(description = "Completion status of the task", example = "false")
    private boolean completed;

    @Schema(description = "Date and time when the task was created", example = "2026-03-24T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date and time when the task was last updated", example = "2026-03-24T12:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Due date for the task", example = "2026-04-01T23:59:59")
    private LocalDateTime dueDate;

    @Schema(description = "Priority of the task", example = "MEDIUM")
    private Priority priority;

    @ArraySchema(schema = @Schema(description = "Tags associated with the task", example = "[\"home\", \"urgent\"]"))
    private Set<String> tags;

    @ArraySchema(schema = @Schema(description = "Attachments associated with the task"))
    private Set<AttachmentResponseDto> attachments;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public Set<AttachmentResponseDto> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<AttachmentResponseDto> attachments) {
        this.attachments = attachments;
    }
}
