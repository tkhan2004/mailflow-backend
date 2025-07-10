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
        try {
            MailReplyDto mailReplyDto = new MailReplyDto();
            mailReplyDto.setThreadId(threadId);
            mailReplyDto.setContent(content);
            mailReplyDto.setFile(file);
            mailService.replyMail(mailReplyDto,sender);
            return ResponseEntity.ok(new ApiResponse<>(200, "Gửi phản hồi thành công", mailReplyDto));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400, "Gửi phản hồi thất bại", null));
        }

    }

    // hộp thoại
    @GetMapping("/inbox")
    public ResponseEntity<ApiResponse<List<MailInboxDto>>> getMails(@AuthenticationPrincipal Users sender) {
        try {
            List<MailInboxDto> MailInboxDto = mailService.getInboxMails(sender);
            return ResponseEntity.ok(new ApiResponse<>(200, "Lấy hộp thoại thành công", MailInboxDto));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400, "Lấy hộp thoại thất bại", null));
        }

    }

    // Xem chi tiết cuộc hội thoại
    @GetMapping("/inbox/thread/{threadId}")
    public ResponseEntity<ApiResponse<MailInboxDetailDto>>  getMailDetails(@PathVariable Long threadId, @AuthenticationPrincipal Users sender) {
        try {
            MailInboxDetailDto mailInboxDetailDto = mailService.getMailDetail(threadId, sender);
            return ResponseEntity.ok(new ApiResponse<>(200, "Chi tiết cuộc hội thoại", mailInboxDetailDto));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400, "Chi tiết cuộc hội thoại thất bại", null));
        }
    }

    @PostMapping("/read-mail")
    public ResponseEntity<ApiResponse<MailInboxDto>> ReadMail(@RequestBody MailStatusRequestDto mailStatusRequestDto, @AuthenticationPrincipal Users sender) {
        try {
            mailService.markMailThreadAsRead(mailStatusRequestDto.getThreadId(), sender);
            return ResponseEntity.ok(new ApiResponse<>(200, "Đánh dấu là đã đọc",null));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400, "Đánh dấu thất bại",null));
        }
    }

    @PostMapping("/delete-threads")
    public ResponseEntity<ApiResponse<?>> DeleteMail(@RequestBody List<Long> threadId, @AuthenticationPrincipal Users users) {
        try {
            mailService.deleteGroup(threadId, users);
            return ResponseEntity.ok(new ApiResponse<>(200, "Thành công xoá cuộc hội thoại khỏi hộp  thư",null));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400, "Đánh dấu thất bại",null));
        }
    }

    @PostMapping("/un-spam-mail")
    public ResponseEntity<ApiResponse<MailInboxDto>> UnSpamMail(@RequestBody MailStatusRequestDto mailStatusRequestDto, @AuthenticationPrincipal Users sender) {
        try {
            mailService.unSpamMailThread(mailStatusRequestDto.getThreadId(), sender);
            return ResponseEntity.ok(new ApiResponse<>(200,"Huỷ spam thành công",null));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>(400,"Huỷ spam thất bại",null));
        }
    }



    @PostMapping("/spam-mail")
    public ResponseEntity<ApiResponse<MailInboxDto>> SpamMail(@RequestBody MailStatusRequestDto mailStatusRequestDto, @AuthenticationPrincipal Users sender) {

        try {
            mailService.markMailThreadAsSpam(mailStatusRequestDto.getThreadId(), sender);
            return ResponseEntity.ok(new ApiResponse<>(200, "Đánh dấu spam",null));
        }
            catch (Exception e){
                return ResponseEntity.ok(new ApiResponse<>(400, "Đánh dấu thất bại",null));
            }
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
            try {
                Long threadId = mailService.createGroup(receiverEmail,subject,sender);
                return ResponseEntity.ok(new ApiResponse<>(200, " Tạo Nhóm thành công", threadId));
            }catch (Exception e){
                return ResponseEntity.ok(new ApiResponse<>(400, " Tạo Nhóm thất bại", null));
            }

    }

}
