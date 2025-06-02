package org.example.mailflowbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MailInboxDetailDto {
    private Long threadId;
    private List<MailResponseDto> mails;
}
