package org.example.mailflowbackend.Dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class MailRequestDto {
    private String receiverEmail;
    private String subject;
    private String content;
    private MultipartFile file;
}
