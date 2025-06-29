package org.example.mailflowbackend.Service.Imp;

import org.example.mailflowbackend.Dto.ProfileResponeDto;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.Repository.UserRepository;
import org.example.mailflowbackend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CloudinaryServiceImp cloudinaryServiceImp;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void updateUsers(String email, String fullname, String phone, MultipartFile avatar) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Mail không tồn tại"));

        user.setFull_name(fullname);
        user.setPhone(phone);
        if (avatar != null &&   !avatar.isEmpty() ) {
            try {
                String avatarUrl = cloudinaryServiceImp.uploadFile(avatar);
                user.setAvatar(avatarUrl);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        userRepository.save(user);
    }

    @Override
    public void changePass(String email,String oldPassword ,String newPassword) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Mail không tồn tại"));

        if (passwordEncoder.matches(oldPassword, user.getPassword() )) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    @Override
    public ProfileResponeDto getProfile(String email) {
        Users user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Mail không tồn tại"));

        ProfileResponeDto profileResponeDto = new ProfileResponeDto(user.getEmail(), user.getFull_name(),user.getPhone(),user.getAvatar());
        return profileResponeDto;
    }

    @Override
    public List<String> seachUsers(String keyword) {
        List<Users> usersSuccess = userRepository.findByEmailContainingIgnoreCase(keyword);
        return usersSuccess.stream().map(Users::getEmail).collect(Collectors.toList());
    }
}
