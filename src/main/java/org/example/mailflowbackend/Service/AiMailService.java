package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Dto.AiMailRequestDto;
import org.example.mailflowbackend.Dto.AiMailResponseDto;

public interface AiMailService {
    public AiMailResponseDto generateAiMail(String prompt);
}
