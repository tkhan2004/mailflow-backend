package org.example.mailflowbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiMailResponseDto {
    private String receiverEmail;
    private String subject;
    private String content;
}
