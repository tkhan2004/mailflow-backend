package org.example.mailflowbackend.Dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    String  email;
    String password;
}
