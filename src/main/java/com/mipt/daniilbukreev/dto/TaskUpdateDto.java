package com.mipt.daniilbukreev.dto;

import com.mipt.daniilbukreev.model.Priority;
import com.mipt.daniilbukreev.validation.DueDateNotBeforeCreation;
import com.mipt.daniilbukreev.validation.OnUpdate;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.Set;

@DueDateNotBeforeCreation(groups = OnUpdate.class)
public class TaskUpdateDto {
    @Size(min = 3, max = 100, groups = OnUpdate.class)
    private String title;

    @Size(max = 500, groups = OnUpdate.class)
    private String description;

    private Boolean completed;

    @FutureOrPresent(groups = OnUpdate.class)
    private LocalDate dueDate;

    @NotNull(groups = OnUpdate.class)
    private Priority priority;

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
