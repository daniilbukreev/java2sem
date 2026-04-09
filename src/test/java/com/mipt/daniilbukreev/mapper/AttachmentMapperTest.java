package com.mipt.daniilbukreev.mapper;

import com.mipt.daniilbukreev.dto.AttachmentResponseDto;
import com.mipt.daniilbukreev.model.TaskAttachment;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AttachmentMapperTest {

    private AttachmentMapper mapper = Mappers.getMapper(AttachmentMapper.class);

    @Test
    void toResponseDto_ShouldMapTaskAttachmentToDto() {
        TaskAttachment attachment = new TaskAttachment("document.pdf", "uuid_document.pdf", "application/pdf", 1024L);
        attachment.setId(1L);
        attachment.setCreatedAt(LocalDateTime.now());

        AttachmentResponseDto dto = mapper.toResponseDto(attachment);

        assertNotNull(dto);
        assertEquals(attachment.getId(), dto.getId());
        assertEquals(attachment.getFileName(), dto.getFileName());
        assertEquals(attachment.getSize(), dto.getSize());
        assertEquals(attachment.getCreatedAt(), dto.getCreatedAt());
    }
}
