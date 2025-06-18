package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.RefreshTokens;
import org.example.mailflowbackend.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    Optional<RefreshTokens> findByToken(String token);
    void deleteByToken(String token);

    Optional<RefreshTokens> findByUsers(Users users);}

