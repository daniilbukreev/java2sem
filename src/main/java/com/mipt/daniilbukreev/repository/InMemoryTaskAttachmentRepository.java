package com.mipt.daniilbukreev.repository;

import com.mipt.daniilbukreev.model.TaskAttachment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryTaskAttachmentRepository implements TaskAttachmentRepository {

    private final Map<Long, TaskAttachment> attachments = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong();

    @Override
    public TaskAttachment save(TaskAttachment attachment) {
        if (attachment.getId() == null) {
            attachment.setId(counter.incrementAndGet());
        }
        attachments.put(attachment.getId(), attachment);
        return attachment;
    }

    @Override
    public Optional<TaskAttachment> findById(Long id) {
        return Optional.ofNullable(attachments.get(id));
    }

    @Override
    public List<TaskAttachment> findByTaskId(Long taskId) {
        return attachments.values().stream()
                .filter(attachment -> attachment.getTaskId().equals(taskId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        attachments.remove(id);
    }
}
