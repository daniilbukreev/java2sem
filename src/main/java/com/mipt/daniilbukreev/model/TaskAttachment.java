package com.mipt.daniilbukreev.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "task_attachments")
@EntityListeners(AuditingEntityListener.class)
public class TaskAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "size")
    private long size;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public TaskAttachment() {
    }

    public TaskAttachment(String fileName, String filePath, String contentType, long size) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.contentType = contentType;
        this.size = size;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskAttachment that = (TaskAttachment) o;
        return size == that.size && Objects.equals(id, that.id) && Objects.equals(fileName, that.fileName) && Objects.equals(filePath, that.filePath) && Objects.equals(contentType, that.contentType) && Objects.equals(uploadedAt, that.uploadedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, fileName, filePath, contentType, size, uploadedAt);
    }

    @Override
    public String toString() {
        return "TaskAttachment{" +
                "id=" + id +
                ", task=" + (task != null ? task.getId() : "null") +
                ", fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", contentType='" + contentType + '\'' +
                ", size=" + size +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
