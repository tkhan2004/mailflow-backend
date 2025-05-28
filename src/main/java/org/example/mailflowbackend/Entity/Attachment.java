package org.example.mailflowbackend.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String file_name;
    private String file_type;
    private Long file_size;
    private String file_url;

    @ManyToOne
    @JoinColumn(name = "mails_id")
    private Mails mails;

}
