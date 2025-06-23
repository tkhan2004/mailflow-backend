package org.example.mailflowbackend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponeDto {
    String email;
    String fullname;
    String phone;
    String avatar;
}
