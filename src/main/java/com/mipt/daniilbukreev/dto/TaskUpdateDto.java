package com.mipt.daniilbukreev.dto;

import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.validation.DueDateNotBeforeCreation;
import com.mipt.daniilbukreev.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO for updating an existing Task")
@DueDateNotBeforeCreation(groups = OnUpdate.class)
public class TaskUpdateDto {
    @Schema(description = "Updated title of the task", example = "Buy milk", nullable = true)
    @Size(min = 3, max = 100, groups = OnUpdate.class)
    private String title;

    @Schema(description = "Updated description of the task", example = "Need 2% milk", nullable = true)
    @Size(max = 500, groups = OnUpdate.class)
    private String description;

    @Schema(description = "Completion status of the task", example = "true", nullable = true)
    private Boolean completed;

    @Schema(description = "Updated due date for the task", example = "2026-04-05", nullable = true)
    @FutureOrPresent(groups = OnUpdate.class)
    private LocalDate dueDate;

    @Schema(description = "Updated priority of the task", example = "HIGH", nullable = true)
    @NotNull(groups = OnUpdate.class)
    private Priority priority;

    @ArraySchema(schema = @Schema(description = "Updated tags associated with the task", example = "[\"dairy\", \"urgent\"]", nullable = true))
    @Size(max = 5, groups = OnUpdate.class)
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

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
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
