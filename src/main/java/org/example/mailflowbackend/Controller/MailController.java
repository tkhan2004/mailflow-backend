package org.example.mailflowbackend.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.mailflowbackend.Dto.*;
import org.example.mailflowbackend.Entity.MailParticipant;
import org.example.mailflowbackend.Entity.MailThread;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.Service.Imp.MailServiceImp;
import org.example.mailflowbackend.Service.MailService;
import org.example.mailflowbackend.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/mail")
public class MailController {
    @Autowired
    private MailServiceImp mailService;

    @Operation(summary = "Gửi email", description = "Gửi email với tệp đính kèm")
    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> sendMail(
            @Parameter(description = "Email người nhận")
            @RequestParam("receiverEmail") String receiverEmail,

            @Parameter(description = "Tiêu đề email")
            @RequestParam("subject") String subject,

            @Parameter(description = "Nội dung email")
            @RequestParam("content") String content,

            @Parameter(description = "File đính kèm", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file,

            @Parameter(description = "Người gửi", hidden = true)
            @AuthenticationPrincipal Users sender) {
        try {
            MailRequestDto mailRequestDto = new MailRequestDto();
            mailRequestDto.setReceiverEmail(receiverEmail);
            mailRequestDto.setSubject(subject);
            mailRequestDto.setContent(content);
            mailRequestDto.setFile(file);

            mailService.sendMail(mailRequestDto, sender);
            return ResponseEntity.ok(new ApiResponse<>(200, "Gửi mail thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(500, "Lỗi khi gửi mail: " + e.getMessage(), null));
        }
    }

    @Operation(summary = "Gửi email", description = "Gửi email với tệp đính kèm")
    @PostMapping(value = "/reply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<?>> replyMail(
            @Parameter(description = "Id cuộc trò chuyện")
            @RequestParam("threadId") Long threadId,

            @Parameter(description = "Nội dung email")
            @RequestParam("content") String content,

            @Parameter(description = "File đính kèm", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "file", required = false) MultipartFile file,

            @AuthenticationPrincipal Users sender
    ) throws Exception {
        MailReplyDto mailReplyDto = new MailReplyDto();
        mailReplyDto.setThreadId(threadId);
        mailReplyDto.setContent(content);
        mailReplyDto.setFile(file);
        mailService.replyMail(mailReplyDto,sender);
        return ResponseEntity.ok(new ApiResponse<>(200, "Gửi phản hồi thành công", mailReplyDto));
    }

    // hộp thoại
    @GetMapping("/inbox")
    public ResponseEntity<ApiResponse<List<MailInboxDto>>> getMails(@AuthenticationPrincipal Users sender) {
        List<MailInboxDto> MailInboxDto = mailService.getInboxMails(sender);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy hộp thoại thành công", MailInboxDto));
    }

    // Xem chi tiết cuộc hội thoại
    @GetMapping("/inbox/thread/{threadId}")
    public ResponseEntity<ApiResponse<MailInboxDetailDto>>  getMailDetails(@PathVariable Long threadId, @AuthenticationPrincipal Users sender) {
        MailInboxDetailDto mailInboxDetailDto = mailService.getMailDetail(threadId, sender);
        return ResponseEntity.ok(new ApiResponse<>(200, "Chi tiết cuộc hội thoại", mailInboxDetailDto));
    }

    @PostMapping("/mail/read-mail")
    public ResponseEntity<ApiResponse<MailInboxDto>> ReadMail(@RequestBody MailStatusRequestDto mailStatusRequestDto, @AuthenticationPrincipal Users sender) {
        mailService.markMailThreadAsRead(mailStatusRequestDto.getThreadId(), sender);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đánh dấu là đã đọc",null));
    }

    @PostMapping("/mail/spam-mail")
    public ResponseEntity<ApiResponse<MailInboxDto>> SpamMail(@RequestBody MailStatusRequestDto mailStatusRequestDto, @AuthenticationPrincipal Users sender) {
        mailService.markMailThreadAsSpam(mailStatusRequestDto.getThreadId(), sender);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đánh dấu spam",null));
    }

    @Operation(summary = "Tạo nhóm gửi mail", description = "Gửi email với tệp đính kèm")
    @PostMapping(value = "/creat-group")
    public ResponseEntity<ApiResponse<Long>> CreatGroup(
            @Parameter(description = "Thêm email vào phòng")
            @RequestParam("receiverEmail") List<String> receiverEmail,

            @Parameter(description = "Tiêu đề email")
            @RequestParam("subject") String subject,

            @Parameter(description = "Người gửi", hidden = true)
            @AuthenticationPrincipal Users sender){
            Long threadId = mailService.createGroup(receiverEmail,subject,sender);
            return ResponseEntity.ok(new ApiResponse<>(200, " Tạo Nhóm thành công", threadId));

    }

}
