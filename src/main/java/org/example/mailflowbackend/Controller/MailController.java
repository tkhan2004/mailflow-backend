package org.example.mailflowbackend.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.mailflowbackend.Dto.MailInboxDetailDto;
import org.example.mailflowbackend.Dto.MailInboxDto;
import org.example.mailflowbackend.Dto.MailRequestDto;
import org.example.mailflowbackend.Dto.MailResponseDto;
import org.example.mailflowbackend.Entity.MailParticipant;
import org.example.mailflowbackend.Entity.MailThread;
import org.example.mailflowbackend.Entity.Users;
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
    private MailService mailService;

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

    @GetMapping("/inbox")
    public ResponseEntity<ApiResponse<List<MailInboxDto>>> getMails(@AuthenticationPrincipal Users sender) {
        List<MailInboxDto> MailInboxDto = mailService.getInboxMails(sender);
        return ResponseEntity.ok(new ApiResponse<>(200, "Lấy hộp thoại thành công", MailInboxDto));
    }

    @GetMapping("/inbox/thread/{threadId}")
    public ResponseEntity<ApiResponse<MailInboxDetailDto>>  getMailDetails(@PathVariable Long threadId, @AuthenticationPrincipal Users sender) {
        MailInboxDetailDto mailInboxDetailDto = mailService.getMailDetail(threadId, sender);
        return ResponseEntity.ok(new ApiResponse<>(200, "Chi tiết cuộc hội thoại", mailInboxDetailDto));
    }
}
