package org.example.mailflowbackend.Dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class MailResponseDto {
    private Long mailId;
    private String subject;
    private String content;
    private String senderEmail;
    private String senderName;
    private String receiverEmail;
    private String receiverName;
    private LocalDateTime createdAt;
    private List<AttachmentResponseDto> attachments;
    private Boolean isRead;
    private Boolean isSpam;
}
