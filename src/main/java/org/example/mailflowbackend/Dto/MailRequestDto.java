package org.example.mailflowbackend.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MailRequestDto {
    @Schema(description = "Danh sách mail nguời nhận")
    private String receiverEmail;

    @Schema(description = "Tiêu đề mail")
    private String subject;

    @Schema(description = "Nội dụng mail")
    private String content;

    @Schema(description = "file đính kèm", type = "string", format = "binary")
    private MultipartFile file;
}
