package org.example.mailflowbackend.Controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.mailflowbackend.Dto.AiMailRequestDto;
import org.example.mailflowbackend.Dto.AiMailResponseDto;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.Service.Imp.AiMailServiceImp;
import org.example.mailflowbackend.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiMailServiceImp aiMailServiceImp;

    @Operation(summary = "Chat với AI hoặc trợ lý soạn mail", description = " Muốn soạn mail vết (hãy giúp tôi soạn mail,... ")
    @PostMapping("/generate-ai")
    public ResponseEntity<ApiResponse<AiMailResponseDto>> generateAiMail(@RequestParam String prompt,
                                                                         @AuthenticationPrincipal Users sender){
        try {
            AiMailResponseDto aiMailResponseDto = aiMailServiceImp.generateAiMail(prompt);
            return ResponseEntity.ok(new ApiResponse<>(200, "Chat thành công", aiMailResponseDto));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok(new ApiResponse<>(400, "Chat thất bại", null));

        }

    }
}
