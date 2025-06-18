package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Dto.AuthRequestDto;
import org.example.mailflowbackend.Dto.LoginResponseDto;
import org.example.mailflowbackend.Dto.RegisterRequestDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface AuthService {
    LoginResponseDto login(AuthRequestDto authRequestDto) throws Exception;
    void register(String full_name, String email, String password,String phone, MultipartFile avatar) throws Exception;
    Boolean isValidEmail(String email);
    public LoginResponseDto refreshAccessToken(AuthRequestDto authRequestDto);
}
