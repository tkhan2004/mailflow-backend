package org.example.mailflowbackend.Service.Imp;

import org.example.mailflowbackend.Dto.AuthRequestDto;
import org.example.mailflowbackend.Dto.LoginResponseDto;
import org.example.mailflowbackend.Entity.RefreshTokens;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.Repository.RefreshTokenRepository;
import org.example.mailflowbackend.Repository.UserRepository;
import org.example.mailflowbackend.Security.JwtUtil;
import org.example.mailflowbackend.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthServiceImp implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CloudinaryServiceImp cloudinaryServiceImp;

    @Autowired
    private RefreshTokenServiceImp refreshTokenServiceImp;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;


    @Override
    public LoginResponseDto login(AuthRequestDto authRequestDto) throws Exception {

        Users user = userRepository.findByEmail(authRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));


        if (! userRepository.findByEmail(authRequestDto.getEmail()).isPresent()) {
            throw new Exception("Sai mật khẩu");
        }

        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshTokenStr = UUID.randomUUID().toString();

        RefreshTokens refreshTokens = new RefreshTokens();
        refreshTokens.setToken(refreshTokenStr);
        refreshTokens.setExpiry_date(LocalDateTime.now().plusDays(7));
        refreshTokens.setUsers(user);
        refreshTokenRepository.save(refreshTokens);

        return new LoginResponseDto(accessToken, refreshTokenStr, user.getEmail());
    }

    @Override
    public void register(String full_name, String email, String password, String phone, MultipartFile avatar) throws Exception {


        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Users user = new Users();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFull_name(full_name);
        user.setPhone(phone);

        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = cloudinaryServiceImp.uploadFile(avatar);
            user.setAvatar(avatarUrl);
        }

        userRepository.save(user);
    }



    @Override
    public Boolean isValidEmail(String email) {
        // Email bình thường
        boolean validFormat = email.matches("^[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$");

        // Không chứa ký tự có dấu tiếng Việt
        String normalized = java.text.Normalizer.normalize(email, java.text.Normalizer.Form.NFD);
        boolean hasDiacritics = normalized.matches(".*\\p{InCombiningDiacriticalMarks}+.*");

        return validFormat && !hasDiacritics;
    }

    @Override
    public LoginResponseDto refreshAccessToken(AuthRequestDto authRequestDto)  {
        Users user = userRepository.findByEmail(authRequestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        RefreshTokens refreshToken = refreshTokenRepository.findByUsers(user)
                .orElseThrow(() -> new RuntimeException("Refresh token không tồn tại"));

        if (!refreshToken.getToken().equals(authRequestDto.getPassword())) {
            throw new RuntimeException("Refresh token không hợp lệ");
        }

        if (refreshTokenServiceImp.isExpired(refreshToken)) {
            refreshTokenRepository.delete(refreshToken); // xoá nếu hết hạn
            throw new RuntimeException("Refresh token đã hết hạn");
        }

        // Nếu hợp lệ → tạo access token mới
        String newAccessToken = jwtUtil.generateToken(user.getEmail());

        return new LoginResponseDto(
                user.getEmail(),
                newAccessToken,
                refreshToken.getToken()
        );
    }
}
