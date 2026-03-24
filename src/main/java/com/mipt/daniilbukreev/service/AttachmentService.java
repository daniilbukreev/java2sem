package com.mipt.daniilbukreev.service;

import com.mipt.daniilbukreev.model.TaskAttachment;
import com.mipt.daniilbukreev.repository.TaskAttachmentRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AttachmentService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskService taskService;

    public AttachmentService(TaskAttachmentRepository attachmentRepository, TaskService taskService) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public TaskAttachment storeAttachment(Long taskId, MultipartFile file) {
        taskService.getTaskByIdOrThrow(taskId);

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.contains("..")) {
            throw new RuntimeException("Invalid file name: " + originalFileName);
        }

        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
        Path targetLocation = Paths.get(uploadDir).resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + originalFileName, e);
        }

        TaskAttachment attachment = new TaskAttachment();
        attachment.setTaskId(taskId);
        attachment.setFileName(originalFileName);
        attachment.setStoredFileName(storedFileName);
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        attachment.setUploadedAt(LocalDateTime.now());

        return attachmentRepository.save(attachment);
    }

    public Optional<TaskAttachment> getAttachment(Long attachmentId) {
        return attachmentRepository.findById(attachmentId);
    }

    public Resource loadAsResource(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with ID: " + attachmentId));

        try {
            Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + attachment.getFileName());
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + attachment.getFileName(), e);
        }
    }

    public void deleteAttachment(Long attachmentId) {
        TaskAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with ID: " + attachmentId));

        Path filePath = Paths.get(uploadDir).resolve(attachment.getStoredFileName()).normalize();
        try {
            Files.deleteIfExists(filePath);
            attachmentRepository.deleteById(attachmentId);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file: " + attachment.getFileName(), e);
        }
    }

    public List<TaskAttachment> getAttachmentsByTaskId(Long taskId) {
        taskService.getTaskByIdOrThrow(taskId);
        return attachmentRepository.findByTaskId(taskId);
    }
}
