package com.mipt.daniilbukreev.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@Table(name = "tasks")
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private boolean completed;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;

    @Column(name = "tags")
    private String tagsString;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<TaskAttachment> attachments = new HashSet<>();

    public Task() {
    }

    public Task(String title, String description, boolean completed, LocalDateTime dueDate, Priority priority, Set<String> tags) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.dueDate = dueDate;
        this.priority = priority;
        this.setTags(tags);
    }

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
        if (this.tagsString == null || this.tagsString.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(this.tagsString.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    public void setTags(Set<String> tags) {
        if (tags == null || tags.isEmpty()) {
            this.tagsString = null;
        } else {
            this.tagsString = String.join(",", tags);
        }
    }

    public Set<TaskAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(Set<TaskAttachment> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(TaskAttachment attachment) {
        this.attachments.add(attachment);
        attachment.setTask(this);
    }

    public void removeAttachment(TaskAttachment attachment) {
        this.attachments.remove(attachment);
        attachment.setTask(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed && Objects.equals(id, task.id) && Objects.equals(title, task.title) && Objects.equals(description, task.description) && Objects.equals(createdAt, task.createdAt) && Objects.equals(dueDate, task.dueDate) && priority == task.priority && Objects.equals(tagsString, task.tagsString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, completed, createdAt, dueDate, priority, tagsString);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", completed=" + completed +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", dueDate=" + dueDate +
                ", priority=" + priority +
                ", tags=" + tagsString +
                '}';
    }
}
