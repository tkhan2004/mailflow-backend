package org.example.mailflowbackend.Service.Imp;

import org.example.mailflowbackend.Entity.RefreshTokens;
import org.example.mailflowbackend.Entity.Users;
import org.example.mailflowbackend.Repository.RefreshTokenRepository;
import org.example.mailflowbackend.Service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RefreshTokenServiceImp implements RefreshTokenService {
    private Long refreshExpirationMs = 3600L * 24 * 7;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshTokens creatRefreshToken(Users users) {
        RefreshTokens refreshTokens = new RefreshTokens();
        refreshTokens.setUsers(users);
        refreshTokens.setToken(UUID.randomUUID().toString());
        refreshTokens.setExpiry_date(LocalDateTime.now().plusSeconds(refreshExpirationMs));
        return refreshTokenRepository.save(refreshTokens);
    }

    public boolean isExpired(RefreshTokens token) {
        return token.getExpiry_date().isBefore(LocalDateTime.now());
    }
}
