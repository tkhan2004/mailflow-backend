package org.example.mailflowbackend.Dto;
import lombok.Data;

@Data
public class RegisterRequestDto {
    private String email;
    private String password;
    private String fullName;
    private String phone;     // mới
    private String avatar;
}
