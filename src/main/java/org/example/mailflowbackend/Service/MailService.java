package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Dto.MailRequestDto;

public interface MailService {
    void sendMail(MailRequestDto mailRequestDto, String senderEmail) throws Exception;
}
