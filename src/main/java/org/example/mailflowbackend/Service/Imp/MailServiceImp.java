package org.example.mailflowbackend.Service.Imp;

import org.example.mailflowbackend.Dto.*;
import org.example.mailflowbackend.Entity.*;
import org.example.mailflowbackend.Repository.*;
import org.example.mailflowbackend.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
            senderParticipant.setRead(false); // Người gửi đã đọc
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
            String encode = encrypt(mailRequestDto.getContent(),4);
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
    public List<MailInboxDto> getInboxMails(Users user) {
        List<MailParticipant> participants = mailParticipantRepository.findMailByUsers(user);

        return participants.stream()
                .map(participant -> {
                    MailThread thread = participant.getThread();

                    // Lấy mail mới nhất trong thread
                    Mails lastMail = mailRepository.findTopByThreadOrderByCreatedAtDesc(thread);

                    if (lastMail == null) {
                        // Nếu chưa có mail nào trong thread, bỏ qua hoặc trả trống
                        return null; // hoặc return new MailInboxDto(..., "", "", "", null, false, false);
                    }

                    return new MailInboxDto(
                            thread.getId(),
                            lastMail.getSubject(),
                            lastMail.getContent(),
                            lastMail.getSender().getEmail(),
                            lastMail.getCreatedAt(),
                            participant.getRead(),
                            participant.getSpam()
                    );
                })
                .filter(Objects::nonNull) // bỏ các null nếu có thread chưa có mail
                .collect(Collectors.toList());
    }


    @Override
    public MailInboxDetailDto getMailDetail(Long threadId, Users user) {
        MailThread mailThread = mailThreadRepository.findById(threadId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cuộc hội thoại"));

        List<Mails> mails = mailRepository.findAllByThreadOrderByCreatedAtAsc(mailThread);

        List<MailResponseDto> mailDto = new ArrayList<>();

        for (Mails mail : mails) {
            MailResponseDto mailResponseDto = new MailResponseDto();
            mailResponseDto.setMailId(mail.getId());
            mailResponseDto.setSenderEmail(mail.getSender().getEmail());
            mailResponseDto.setReceiverEmail(mail.getReceiver().getEmail());
            mailResponseDto.setSenderName(mail.getSender().getFull_name());
            mailResponseDto.setReceiverName(mail.getReceiver().getFull_name());
            mailResponseDto.setSubject(mail.getSubject());
            mailResponseDto.setContent(decrypt(mail.getContent(), 4)); // giả sử có method decrypt
            mailResponseDto.setCreatedAt(mail.getCreatedAt());

            MailParticipant participant = mailParticipantRepository.findByThreadAndUsers((mails.getLast().getThread()), user);
            if (participant != null) {
                mailResponseDto.setIsRead(participant.getRead());
                mailResponseDto.setIsSpam(participant.getSpam());
            }

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
    public void markMailAsRead(Long mailId, Users user) {

    }

    @Override
    public void markMailAsSpam(Long mailId, Users user) {

    }




    // mã hoá thay thế cộng ceasar
    private String encrypt(String text, int key){
        StringBuilder result = new StringBuilder();
        for (char i : text.toCharArray()){
            if(Character.isUpperCase(i)){
                char encrypt = (char) ((i - 'A' + key)%26 +'A');
                result.append(encrypt);
            } else if (Character.isLowerCase(i)) {
                char encrypt = (char) ((i - 'a' + key)%26 + 'a');
                result.append(encrypt);
            }else {
                result.append(i);
            }
        };
        return result.toString();
    }

    public String decrypt(String content, int key) {
        StringBuilder result = new StringBuilder();

        for (char c : content.toCharArray()) {
            // Giải mã chữ thường
            if (c >= 'a' && c <= 'z') {
                result.append((char) ('a' + (c - 'a' - key + 26) % 26));
            }
            // Giải mã chữ hoa
            else if (c >= 'A' && c <= 'Z') {
                result.append((char) ('A' + (c - 'A' - key + 26) % 26));
            }
            // Không mã hóa
            else {
                result.append(c);
            }
        }

        return result.toString();
    }
}
