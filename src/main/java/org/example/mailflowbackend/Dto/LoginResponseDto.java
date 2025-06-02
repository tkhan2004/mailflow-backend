package org.example.mailflowbackend.Dto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String accessToken;
    private String refreshToken;
    private String email;

    public LoginResponseDto(String accessToken, String refreshTokenStr, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshTokenStr;
        this.email = email;
    }


}
