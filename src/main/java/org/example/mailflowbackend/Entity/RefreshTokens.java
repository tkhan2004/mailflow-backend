package org.example.mailflowbackend.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name ="refresh_tokens")
public class RefreshTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    private String token;
    private LocalDateTime expiry_date;
    private LocalDateTime create_at;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

}
