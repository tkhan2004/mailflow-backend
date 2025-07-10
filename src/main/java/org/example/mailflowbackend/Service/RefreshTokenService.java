package org.example.mailflowbackend.Service;

import org.example.mailflowbackend.Entity.RefreshTokens;
import org.example.mailflowbackend.Entity.Users;

public interface RefreshTokenService {
    public RefreshTokens creatRefreshToken(Users users);
    public boolean isExpired(RefreshTokens token);
}
