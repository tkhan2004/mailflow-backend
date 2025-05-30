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
    @JoinColumn(name = "users_id")
    private Users users;

    // Constructors
    public RefreshTokens() {
    }

    public RefreshTokens(String token, LocalDateTime expiry_date, LocalDateTime create_at, Users users) {
        this.token = token;
        this.expiry_date = expiry_date;
        this.create_at = create_at;
        this.users = users;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(LocalDateTime expiry_date) {
        this.expiry_date = expiry_date;
    }

    public LocalDateTime getCreate_at() {
        return create_at;
    }

    public void setCreate_at(LocalDateTime create_at) {
        this.create_at = create_at;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
