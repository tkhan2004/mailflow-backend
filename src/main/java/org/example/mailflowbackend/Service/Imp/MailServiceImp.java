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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MailServiceImp implements MailService {
    @Autowired
    private CloudinaryServiceImp cloudinaryServiceImp;

    @Autowired
    private MailRepository mailRepository;

    @Autowired
    private MailThreadRepository mailThreadRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AttachmentRepository attachmentRepository;

    @Autowired
    private MailParticipantRepository mailParticipantRepository;



    @Override
    @Transactional
    public void sendMail(MailRequestDto mailRequestDto, Users sender) throws Exception {
        try {
            // Get the receiver user from the database
            Users receiver = userRepository.findByEmail(mailRequestDto.getReceiverEmail())
                    .orElseThrow(() -> new RuntimeException("Người nhận không tồn tại: " + mailRequestDto.getReceiverEmail()));

            // Create a new mail thread
            MailThread mailThread = new MailThread(mailRequestDto.getSubject());
            mailThreadRepository.save(mailThread);

            // Tạo MailParticipant cho sender
            MailParticipant senderParticipant = new MailParticipant();
            senderParticipant.setThread(mailThread);
            senderParticipant.setUsers(sender);
            senderParticipant.setRead(true); // Người gửi đã đọc
            senderParticipant.setSpam(false);
            mailParticipantRepository.save(senderParticipant);

            // Tạo MailParticipant cho receiver
            MailParticipant receiverParticipant = new MailParticipant();
            receiverParticipant.setThread(mailThread);
            receiverParticipant.setUsers(receiver);
            receiverParticipant.setRead(false); // Người nhận chưa đọc
            receiverParticipant.setSpam(false);
            mailParticipantRepository.save(receiverParticipant);

            // Create a new mail
            Mails mail = new Mails();
            mail.setSubject(mailRequestDto.getSubject());
            String encode = encrypt(mailRequestDto.getContent());
            mail.setContent(encode);
            mail.setCreatedAt(LocalDateTime.now());
            mail.setSender(sender);
            mail.setReceiver(receiver);
            mail.setThread(mailThread);

            // Handle attachment if present
            MultipartFile file = mailRequestDto.getFile();
            if (file != null && !file.isEmpty()) {
                String fileUrl = cloudinaryServiceImp.uploadFile(file);

                Attachment attachment = new Attachment();
                attachment.setFile_name(file.getOriginalFilename());
                attachment.setFile_type(file.getContentType());
                attachment.setFile_size(file.getSize());
                attachment.setFile_url(fileUrl);
                attachment.setMails(mail);

                mail.getAttachments().add(attachment);
            }

            // Save the mail
            mailRepository.save(mail);
        } catch (Exception e) {
            throw new Exception("Lỗi khi gửi mail: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void replyMail(MailReplyDto mailReplyDto, Users sender) throws Exception {
        MailThread mailThread = mailThreadRepository.findById(mailReplyDto.getThreadId()).orElseThrow(() -> new RuntimeException("Không tìm thấy hộp thoại"));

        Mails lastMail = mailRepository.findTopByThreadOrderByCreatedAtDesc(mailThread);
        if (lastMail == null) {
            throw new RuntimeException("Không tìm thấy mail nào trong cuộc hội thoại");
        }

        Users receiver = lastMail.getSender().getId().equals(sender.getId()) ? lastMail.getReceiver() : lastMail.getSender();
        Mails replyEmail = new Mails();
        replyEmail.setThread(mailThread);
        replyEmail.setSender(sender);
        replyEmail.setReceiver(receiver);
        replyEmail.setSubject(mailThread.getTitle().startsWith("Re: ")? mailThread.getTitle() : "Re: "+ mailThread.getTitle());
        replyEmail.setContent(encrypt(mailReplyDto.getContent()));
        replyEmail.setCreatedAt(LocalDateTime.now());

        MultipartFile file = mailReplyDto.getFile();
        if (file != null && !file.isEmpty()) {
            String fileUrl = cloudinaryServiceImp.uploadFile(file);

            Attachment attachment = new Attachment();
            attachment.setFile_name(file.getOriginalFilename());
            attachment.setFile_type(file.getContentType());
            attachment.setFile_size(file.getSize());
            attachment.setFile_url(fileUrl);
            attachment.setMails(replyEmail);

            replyEmail.getAttachments().add(attachment);
        }

        MailParticipant senderPart = mailParticipantRepository.findByThreadAndUsers(mailThread, sender);
        if (senderPart == null) {
            senderPart = new MailParticipant();
            senderPart.setThread(mailThread);
            senderPart.setUsers(sender);
            senderPart.setRead(true);
            senderPart.setSpam(false);
            mailParticipantRepository.save(senderPart);
        }

        MailParticipant receiverPart = mailParticipantRepository.findByThreadAndUsers(mailThread, receiver);
        if (receiverPart == null) {
            receiverPart = new MailParticipant();
            receiverPart.setThread(mailThread);
            receiverPart.setUsers(receiver);
            receiverPart.setRead(false);
            receiverPart.setSpam(false);
            mailParticipantRepository.save(receiverPart);
        } else {
            receiverPart.setRead(false);
            mailParticipantRepository.save(receiverPart);
        }

        mailRepository.save(replyEmail);




    }


    @Override
    public List<MailInboxDto> getInboxMails(Users user) {
        List<MailParticipant> participants = mailParticipantRepository.findMailByUsers(user);

        return participants.stream()
                .map(participant -> {
                    MailThread thread = participant.getThread();

                    // Lấy mail mới nhất trong thread
                    Mails lastMail = mailRepository.findTopByThreadOrderByCreatedAtDesc(thread);

                    if (lastMail == null) {
                        return null;
                    }

                    // Giải mã nội dung
                    String decryptedContent = (lastMail.getContent() != null)
                            ? decrypt(lastMail.getContent())
                            : "";

                    return new MailInboxDto(
                            thread.getId(),
                            lastMail.getSubject(),
                            decryptedContent, // ✅ Đã giải mã
                            lastMail.getSender().getEmail(),
                            lastMail.getReceiver().getEmail(),
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

        // Tìm thông tin participant 1 lần
        MailParticipant participant = mailParticipantRepository.findByThreadAndUsers(mailThread, user);
        if (participant != null && !participant.getRead()) {
            participant.setRead(true); // đánh dấu đã đọc
            mailParticipantRepository.save(participant);
        }

        for (Mails mail : mails) {
            MailResponseDto mailResponseDto = new MailResponseDto();
            mailResponseDto.setMailId(mail.getId());
            mailResponseDto.setSenderEmail(mail.getSender().getEmail());
            mailResponseDto.setReceiverEmail(mail.getReceiver().getEmail());
            mailResponseDto.setSenderName(mail.getSender().getFull_name());
            mailResponseDto.setReceiverName(mail.getReceiver().getFull_name());
            mailResponseDto.setSubject(mail.getSubject());
            mailResponseDto.setContent(decrypt(mail.getContent()));
            mailResponseDto.setCreatedAt(mail.getCreatedAt());

            // Gắn trạng thái read/spam theo participant
            if (participant != null) {
                mailResponseDto.setIsRead(participant.getRead());
                mailResponseDto.setIsSpam(participant.getSpam());
            }

            // Gắn đính kèm
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
        List<MailParticipant> participants = mailParticipantRepository.findByThread_IdInAndUsers(threadId,user);
        participants.forEach(participant -> {participant.setRead(true);});
        mailParticipantRepository.saveAll(participants);
    }

    @Override
    public void markMailThreadAsSpam(List<Long> threadId, Users user) {
        List<MailParticipant> participants = mailParticipantRepository.findByThread_IdInAndUsers(threadId,user);
        participants.forEach(participant -> {participant.setSpam(true);});
        mailParticipantRepository.saveAll(participants);
    }

    @Override
    public Long createGroup(List<String> emails, String title, Users creator) {
        MailThread mailThread = new MailThread();
        mailThread.setTitle(title);
        mailThreadRepository.save(mailThread);

        MailParticipant participant = new MailParticipant(mailThread,creator,true,false);
        mailParticipantRepository.save(participant);

        List<Users>  orther = userRepository.findAllByEmailIn(emails) ;
        for (Users user : orther) {
            MailParticipant mailParticipant = new MailParticipant(mailThread,user,true,false);
            mailParticipantRepository.save(mailParticipant);
        }

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
