package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Dto.ProfileResponeDto;
import org.example.mailflowbackend.Entity.Users;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    void updateUsers(String email, String fullname, String phone, MultipartFile avatar);
    void changePass(String email,String oldPassword ,String newPassword);
    ProfileResponeDto getProfile(String email);
    List<String> seachUsers(String keyword);

}
