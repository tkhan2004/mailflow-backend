package org.example.mailflowbackend.Controller;

import org.example.mailflowbackend.Dto.MailRequestDto;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.payload.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MailController {

    @PostMapping(value = "/mail/send")
    public ResponseEntity<ApiResponse<?>> sendMails(@ModelAttribute MailRequestDto mailRequestDto, @AuthenticationPrincipal Users users){

    }
}
