package org.example.mailflowbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MailInboxDto {
    private Long threadId;
    private String subject;
    private String lastContent;
    private String lastSenderEmail;
    private LocalDateTime lastCreatedAt;
    private boolean isRead;
    private boolean isSpam;
}