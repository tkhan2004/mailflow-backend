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
    @Autowired private NotificationServiceImp notificationServiceImp;

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
        notificationServiceImp.notify(receiver.getEmail()," Bạn vừa có 1 thư mới từ " + sender.getEmail());
    }

    @Override
    @Transactional
    public void replyMail(MailReplyDto mailReplyDto, Users sender) throws Exception {
        MailThread thread = mailThreadRepository.findById(mailReplyDto.getThreadId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

        // Tạo mail mới
        Mails mail = new Mails();
        mail.setThread(thread);
        mail.setSender(sender);
        mail.setSubject(thread.getTitle().startsWith("Re: ") ? thread.getTitle() : "Re: " + thread.getTitle());
        mail.setContent(encrypt(mailReplyDto.getContent()));
        mail.setCreatedAt(LocalDateTime.now());
        mail.setReceiver(null); // Vì là nhóm

        MultipartFile file = mailReplyDto.getFile();
        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setFile_name(file.getOriginalFilename());
            attachment.setFile_type(file.getContentType());
            attachment.setFile_size(file.getSize());
            attachment.setFile_url(cloudinaryServiceImp.uploadFile(file));
            attachment.setMails(mail);
            mail.getAttachments().add(attachment);
        }

        // Lưu mail
        mailRepository.save(mail);


        // Cập nhật trạng thái read của các thành viên
        List<MailParticipant> participants = mailParticipantRepository.findByThreadId(thread.getId());
        for (MailParticipant p : participants) {
            if (p.getUsers().getId().equals(sender.getId())) {
                p.setRead(true); // Người gửi đã đọc
            } else {
                p.setRead(false); // Những người còn lại chưa đọc
            }
            mailParticipantRepository.save(p);
        }
        for (MailParticipant p : participants) {
            if (!p.getUsers().getId().equals(sender.getId())) {
                notificationServiceImp.notify(
                        p.getUsers().getEmail(),
                        "📨 Nhóm \"" + thread.getTitle() + "\" vừa có tin nhắn mới"
                );
            }
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
                    String receiverEmail = isGroup
                            ? thread.getTitle()
                            : Optional.ofNullable(lastMail.getReceiver())
                            .map(Users::getEmail)
                            .orElse("(không xác định)");

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
        MailThread thread = mailThreadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

        // ✅ Đánh dấu là đã đọc
        MailParticipant participant = mailParticipantRepository.findByThreadAndUsers(thread, user);
        if (participant != null && !participant.getRead()) {
            participant.setRead(true);
            mailParticipantRepository.save(participant);
        }

        List<Mails> mails = mailRepository.findAllByThreadOrderByCreatedAtAsc(thread);
        List<MailResponseDto> mailDtoList = new ArrayList<>();

        for (Mails mail : mails) {
            MailResponseDto dto = new MailResponseDto();
            dto.setMailId(mail.getId());
            dto.setSubject(mail.getSubject());
            dto.setContent(decrypt(mail.getContent()));
            dto.setCreatedAt(mail.getCreatedAt());

            dto.setSenderEmail(mail.getSender().getEmail());
            dto.setSenderName(mail.getSender().getFull_name());

            if (mail.getReceiver() != null) {
                dto.setReceiverEmail(mail.getReceiver().getEmail());
                dto.setReceiverName(mail.getReceiver().getFull_name());
            } else {
                dto.setReceiverEmail("Nhóm");
                dto.setReceiverName("Tất cả thành viên");
            }

            if (participant != null) {
                dto.setIsRead(participant.getRead());
                dto.setIsSpam(participant.getSpam());
            }

            if (mail.getAttachments() != null && !mail.getAttachments().isEmpty()) {
                dto.setAttachments(
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

            mailDtoList.add(dto);
        }

        return new MailInboxDetailDto(threadId, mailDtoList);
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

        for (Users user : receivers) {
            if( !user.getId().equals(creator.getId())){
                notificationServiceImp.notify(user.getEmail(),"Bạn vừa được thêm vào nhóm " + title);
            }
        }

        return mailThread.getId();
    }

    @Override
    public void deleteGroup(List<Long> threadId, Users user) {
        List<MailParticipant> list = mailParticipantRepository.findByThread_IdInAndUsers(threadId, user);
        list.forEach(participant -> participant.setDeleted(true));
        mailParticipantRepository.saveAll(list);
    }

    public String encrypt(String content) {
        return Base64.getEncoder().encodeToString(content.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(String encoded) {
        byte[] decoded = Base64.getDecoder().decode(encoded);
        return new String(decoded, StandardCharsets.UTF_8);
    }
}