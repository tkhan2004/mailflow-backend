package org.example.mailflowbackend.Controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.example.mailflowbackend.Dto.AuthRequestDto;
import org.example.mailflowbackend.Dto.LoginResponseDto;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.Repository.UserRepository;
import org.example.mailflowbackend.Security.JwtUtil;
import org.example.mailflowbackend.Service.Imp.AuthServiceImp;
import org.example.mailflowbackend.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthServiceImp authServiceImp;

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<?>> register(
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart("fullName") String fullName,
            @RequestPart("phone") String phone,
            @Parameter(description = "Avatar upload", schema = @Schema(type = "string", format = "binary"))
            @RequestPart(value = "avatar", required = false) MultipartFile avatar
    ) throws Exception {
        authServiceImp.register(fullName, email, password, phone, avatar);
        return ResponseEntity.ok(new ApiResponse<>(200, "Đăng ký thành công", null));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse<LoginResponseDto>> login(@RequestBody AuthRequestDto authRequestDto) throws Exception {
        LoginResponseDto loginResponseDto = authServiceImp.login(authRequestDto);
        return ResponseEntity.ok( new ApiResponse<>(200, "Đăng nhập thành công", loginResponseDto));
    }

}
