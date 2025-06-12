package org.example.mailflowbackend.Dto;

import lombok.Data;

import java.util.List;

@Data
public class MailStatusRequestDto {
    private List<Long> threadId;
}
