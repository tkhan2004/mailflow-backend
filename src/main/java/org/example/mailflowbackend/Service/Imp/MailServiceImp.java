package org.example.mailflowbackend.Service.Imp;

import org.example.mailflowbackend.Dto.*;
import org.example.mailflowbackend.Entity.*;
import org.example.mailflowbackend.Repository.*;
import org.example.mailflowbackend.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MailServiceImp implements MailService {
    @Autowired private CloudinaryServiceImp cloudinaryServiceImp;
    @Autowired private MailRepository mailRepository;
    @Autowired private MailThreadRepository mailThreadRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AttachmentRepository attachmentRepository;
    @Autowired private MailParticipantRepository mailParticipantRepository;

    @Override
    @Transactional
    public void sendMail(MailRequestDto mailRequestDto, Users sender) throws Exception {
        Users receiver = userRepository.findByEmail(mailRequestDto.getReceiverEmail())
                .orElseThrow(() -> new RuntimeException("Người nhận không tồn tại"));

        MailThread mailThread = new MailThread(mailRequestDto.getSubject());
        mailThreadRepository.save(mailThread);

        mailParticipantRepository.save(new MailParticipant(mailThread, sender, true, false));
        mailParticipantRepository.save(new MailParticipant(mailThread, receiver, false, false));

        Mails mail = new Mails();
        mail.setSubject(mailRequestDto.getSubject());
        mail.setContent(encrypt(mailRequestDto.getContent()));
        mail.setCreatedAt(LocalDateTime.now());
        mail.setSender(sender);
        mail.setReceiver(receiver);
        mail.setThread(mailThread);

        MultipartFile file = mailRequestDto.getFile();
        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setFile_name(file.getOriginalFilename());
            attachment.setFile_type(file.getContentType());
            attachment.setFile_size(file.getSize());
            attachment.setFile_url(cloudinaryServiceImp.uploadFile(file));
            attachment.setMails(mail);
            mail.getAttachments().add(attachment);
        }

        mailRepository.save(mail);
    }

    @Override
    @Transactional
    public void replyMail(MailReplyDto mailReplyDto, Users sender) throws Exception {
        MailThread thread = mailThreadRepository.findById(mailReplyDto.getThreadId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

        // Tạo mail mới (dạng nhóm)
        Mails reply = new Mails();
        reply.setThread(thread);
        reply.setSender(sender);
        reply.setSubject(thread.getTitle().startsWith("Re: ") ? thread.getTitle() : "Re: " + thread.getTitle());
        reply.setContent(encrypt(mailReplyDto.getContent()));
        reply.setCreatedAt(LocalDateTime.now());
        reply.setReceiver(null); // ❗️Vì đây là nhóm, không cần receiver cụ thể

        // Đính kèm nếu có
        MultipartFile file = mailReplyDto.getFile();
        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setFile_name(file.getOriginalFilename());
            attachment.setFile_type(file.getContentType());
            attachment.setFile_size(file.getSize());
            attachment.setFile_url(cloudinaryServiceImp.uploadFile(file));
            attachment.setMails(reply);
            reply.getAttachments().add(attachment);
        }

        // Lưu mail (chỉ 1 bản ghi)
        mailRepository.save(reply);

        // Cập nhật trạng thái read của các participant
        List<MailParticipant> participants = mailParticipantRepository.findByThreadId(thread.getId());
        for (MailParticipant part : participants) {
            if (part.getUsers().getId().equals(sender.getId())) {
                part.setRead(true); // người gửi đã đọc
            } else {
                part.setRead(false); // người khác chưa đọc
            }
            mailParticipantRepository.save(part);
        }
    }
    @Override
    public List<MailInboxDto> getInboxMails(Users user) {
        List<MailParticipant> participants = mailParticipantRepository.findMailByUsers(user);
        return participants.stream()
                .map(participant -> {
                    MailThread thread = participant.getThread();
                    Mails lastMail = mailRepository.findTopByThreadOrderByCreatedAtDesc(thread);
                    if (lastMail == null) return null;

                    String content = lastMail.getContent() != null ? decrypt(lastMail.getContent()) : "";
                    boolean isGroup = mailParticipantRepository.findByThreadId(thread.getId()).size() > 2;
                    String receiverEmail = isGroup ? thread.getTitle() : lastMail.getReceiver().getEmail();

                    List<String> group = isGroup ?
                            mailParticipantRepository.findByThreadId(thread.getId())
                                    .stream().map(p -> p.getUsers().getEmail()).collect(Collectors.toList())
                            : null;

                    return new MailInboxDto(
                            thread.getId(),
                            thread.getTitle(),
                            lastMail.getSubject(),
                            content,
                            lastMail.getSender().getEmail(),
                            receiverEmail,
                            group,
                            lastMail.getCreatedAt(),
                            participant.getRead(),
                            participant.getSpam()
                    );
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public MailInboxDetailDto getMailDetail(Long threadId, Users user) {
        MailThread mailThread = mailThreadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

        List<Mails> mails = mailRepository.findAllByThreadOrderByCreatedAtAsc(mailThread);
        List<MailResponseDto> mailDto = new ArrayList<>();

        // Đánh dấu đã đọc
        MailParticipant participant = mailParticipantRepository.findByThreadAndUsers(mailThread, user);
        if (participant != null && !participant.getRead()) {
            participant.setRead(true);
            mailParticipantRepository.save(participant);
        }

        for (Mails mail : mails) {
            MailResponseDto mailResponseDto = new MailResponseDto();
            mailResponseDto.setMailId(mail.getId());
            mailResponseDto.setSubject(mail.getSubject());
            mailResponseDto.setContent(decrypt(mail.getContent()));
            mailResponseDto.setCreatedAt(mail.getCreatedAt());

            mailResponseDto.setSenderEmail(mail.getSender().getEmail());
            mailResponseDto.setSenderName(mail.getSender().getFull_name());

            // ✅ Check null receiver
            if (mail.getReceiver() != null) {
                mailResponseDto.setReceiverEmail(mail.getReceiver().getEmail());
                mailResponseDto.setReceiverName(mail.getReceiver().getFull_name());
            } else {
                mailResponseDto.setReceiverEmail("Nhóm");
                mailResponseDto.setReceiverName("Tất cả thành viên");
            }

            if (participant != null) {
                mailResponseDto.setIsRead(participant.getRead());
                mailResponseDto.setIsSpam(participant.getSpam());
            }

            // Đính kèm nếu có
            if (mail.getAttachments() != null && !mail.getAttachments().isEmpty()) {
                mailResponseDto.setAttachments(
                        mail.getAttachments().stream().map(att -> {
                            AttachmentResponseDto attDto = new AttachmentResponseDto();
                            attDto.setFileName(att.getFile_name());
                            attDto.setFileSize(att.getFile_size());
                            attDto.setFileType(att.getFile_type());
                            attDto.setFileUrl(att.getFile_url());
                            return attDto;
                        }).collect(Collectors.toList())
                );
            }

            mailDto.add(mailResponseDto);
        }

        return new MailInboxDetailDto(threadId, mailDto);
    }

    @Override
    public void markMailThreadAsRead(List<Long> threadId, Users user) {
        List<MailParticipant> list = mailParticipantRepository.findByThread_IdInAndUsers(threadId, user);
        list.forEach(p -> p.setRead(true));
        mailParticipantRepository.saveAll(list);
    }

    @Override
    public void markMailThreadAsSpam(List<Long> threadId, Users user) {
        List<MailParticipant> list = mailParticipantRepository.findByThread_IdInAndUsers(threadId, user);
        list.forEach(p -> p.setSpam(true));
        mailParticipantRepository.saveAll(list);
    }

    @Override
    public Long createGroup(List<String> emails, String title, Users creator) {
        MailThread mailThread = new MailThread();
        mailThread.setTitle(title);
        mailThreadRepository.save(mailThread);

        // Thêm người tạo vào group
        MailParticipant creatorParticipant = new MailParticipant(mailThread, creator, true, false);
        mailParticipantRepository.save(creatorParticipant);

        List<Users> receivers = userRepository.findAllByEmailIn(emails);

        for (Users user : receivers) {
            MailParticipant participant = new MailParticipant(mailThread, user, false, false);
            mailParticipantRepository.save(participant);
        }

        // 👉 Tạo 1 Mails duy nhất
        Mails introMail = new Mails();
        introMail.setThread(mailThread);
        introMail.setSender(creator);
        introMail.setSubject("Re: " + title);
        introMail.setContent(encrypt("Nhóm \"" + title + "\" đã được tạo"));
        introMail.setCreatedAt(LocalDateTime.now());

        // 👇 KHÔNG setReceiver nữa, vì đây là mail nhóm
        mailRepository.save(introMail);

        return mailThread.getId();
    }

    public String encrypt(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(String encoded) {
        byte[] decoded = Base64.getDecoder().decode(encoded);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}