package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Dto.MailInboxDetailDto;
import org.example.mailflowbackend.Dto.MailInboxDto;
import org.example.mailflowbackend.Dto.MailRequestDto;
import org.example.mailflowbackend.Entity.Users;

import java.util.List;

public interface MailService {
    void sendMail(MailRequestDto mailRequestDto, Users sender) throws Exception;

    List<MailInboxDto> getInboxMails(Users user);

    MailInboxDetailDto getMailDetail(Long threadId, Users user);

    void markMailAsRead(Long mailId, Users user);

    void markMailAsSpam(Long mailId, Users user);
}
