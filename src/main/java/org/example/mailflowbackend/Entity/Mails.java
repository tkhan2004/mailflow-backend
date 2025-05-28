package org.example.mailflowbackend.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name =  "mails")
public class Mails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String content;
    private LocalDateTime created_at;

    @OneToMany(mappedBy = "mails", cascade = CascadeType.ALL)
    private List<Attachment>  attachments;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Users sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Users receiver;



}
