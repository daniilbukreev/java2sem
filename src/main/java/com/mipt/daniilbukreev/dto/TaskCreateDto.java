package com.mipt.daniilbukreev.dto;

import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.validation.OnCreate;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Set;

@Schema(description = "DTO for creating a new Task")
public class TaskCreateDto {
    @Schema(description = "Title of the task", example = "Buy groceries")
    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 100, groups = OnCreate.class)
    private String title;

    @Schema(description = "Description of the task", example = "Milk, Eggs, Bread", nullable = true)
    @Size(max = 500, groups = OnCreate.class)
    private String description;

    @Schema(description = "Due date for the task", example = "2026-04-01T12:00:00", nullable = true)
    @FutureOrPresent(groups = OnCreate.class)
    private LocalDateTime dueDate;

    @Schema(description = "Priority of the task", example = "MEDIUM")
    @NotNull(groups = OnCreate.class)
    private Priority priority;

    @ArraySchema(schema = @Schema(description = "Tags associated with the task", example = "[\"home\", \"urgent\"]", nullable = true))
    @Size(max = 5, groups = OnCreate.class)
    private Set<String> tags;

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
}
