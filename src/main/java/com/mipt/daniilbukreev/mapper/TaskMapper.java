package com.mipt.daniilbukreev.mapper;

import com.mipt.daniilbukreev.dto.TaskCreateDto;
import com.mipt.daniilbukreev.dto.TaskResponseDto;
import com.mipt.daniilbukreev.dto.TaskUpdateDto;
import com.mipt.daniilbukreev.model.Task;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AttachmentMapper.class}
)
public interface TaskMapper {

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Task toEntity(TaskCreateDto dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Task updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

    @Mapping(target = "tags", expression = "java(task.getTags())")
    @Mapping(target = "dueDate", source = "dueDate")
    @Mapping(target = "attachments", source = "attachments")
    TaskResponseDto toResponseDto(Task task);

    @AfterMapping
    default void setTagsFromCreateDto(TaskCreateDto dto, @MappingTarget Task task) {
        if (dto.getTags() != null) {
            task.setTags(dto.getTags());
        }
    }

    @AfterMapping
    default void setTagsFromUpdateDto(TaskUpdateDto dto, @MappingTarget Task task) {
        if (dto.getTags() != null) {
            task.setTags(dto.getTags());
        }
    }
}
