package com.mipt.daniilbukreev.mapper;

import com.mipt.daniilbukreev.dto.AttachmentResponseDto;
import com.mipt.daniilbukreev.model.TaskAttachment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentResponseDto toResponseDto(TaskAttachment attachment);
}
