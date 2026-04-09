package com.mipt.daniilbukreev.validation;

import com.mipt.daniilbukreev.dto.TaskUpdateDto;
import com.mipt.daniilbukreev.model.Task;
import com.mipt.daniilbukreev.repository.TaskRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import java.time.LocalDateTime;
import java.util.Map;

public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        if (dto.getDueDate() == null) {
            return true;
        }

        Map<String, String> pathVariables = (Map<String, String>) RequestContextHolder.getRequestAttributes()
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        if (pathVariables == null || !pathVariables.containsKey("id")) {
            return true;
        }

        Long taskId = Long.valueOf(pathVariables.get("id"));
        return taskRepository.findById(taskId)
                .map(Task::getCreatedAt)
                .map(createdAt -> {
                    LocalDateTime creationDateTime = createdAt;
                    return dto.getDueDate().isAfter(creationDateTime) || dto.getDueDate().isEqual(creationDateTime);
                })
                .orElse(true);
    }
}
