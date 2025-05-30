package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.RefreshTokens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    RefreshTokens findByToken(String refreshTokenStr);
}

