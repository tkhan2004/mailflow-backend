package org.example.mailflowbackend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MailReplyDto {
    @Schema(description = "Id cuộc hội thoại reply")
    private Long threadId;

    @Schema(description = "Nội dụng mail")
    private String content;

    @Schema(description = "file đính kèm", type = "string", format = "binary")
    private MultipartFile file;
}
