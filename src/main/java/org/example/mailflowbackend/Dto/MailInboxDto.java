package org.example.mailflowbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class MailInboxDto {
    private Long threadId;
    private String title;
    private String subject;
    private String lastContent;
    private String lastSenderEmail;
    private String lastReceiverEmail;
    private List<String> groupMembers; // Nếu là nhóm thì sẽ có
    private LocalDateTime lastCreatedAt;
    private boolean isRead;
    private boolean isSpam;
}