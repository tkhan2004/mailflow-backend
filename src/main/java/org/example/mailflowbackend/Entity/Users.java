package org.example.mailflowbackend.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name ="users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String full_name;

    @OneToMany(mappedBy = "sender")
    private List<Mails> sendMails;

    @OneToMany(mappedBy = "receiver")
    private List<Mails> receiveMails;

    @OneToMany(mappedBy = "users",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshTokens> refreshTokens = new ArrayList<>();

}
