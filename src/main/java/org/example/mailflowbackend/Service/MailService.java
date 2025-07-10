package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Dto.MailInboxDetailDto;
import org.example.mailflowbackend.Dto.MailInboxDto;
import org.example.mailflowbackend.Dto.MailReplyDto;
import org.example.mailflowbackend.Dto.MailRequestDto;
import org.example.mailflowbackend.Entity.Users;

import java.util.List;

public interface MailService {
    void sendMail(MailRequestDto mailRequestDto, Users sender) throws Exception;

    void replyMail(MailReplyDto mailReplyDto, Users sender) throws Exception;

    List<MailInboxDto> getInboxMails(Users user);

    MailInboxDetailDto getMailDetail(Long threadId, Users user);

    void markMailThreadAsRead(List<Long> threadId, Users user);

    void markMailThreadAsSpam(List<Long> threadId, Users user);

    void unSpamMailThread(List<Long> threadId, Users user);

    Long createGroup(List<String> emails,String title ,Users creator);

    void deleteGroup(List<Long> threadId, Users user);

}
