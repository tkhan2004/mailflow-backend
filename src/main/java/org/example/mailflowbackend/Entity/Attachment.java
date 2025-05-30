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

    // Constructors
    public Attachment() {
    }

    public Attachment(String file_name, String file_type, Long file_size, String file_url, Mails mails) {
        this.file_name = file_name;
        this.file_type = file_type;
        this.file_size = file_size;
        this.file_url = file_url;
        this.mails = mails;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public Long getFile_size() {
        return file_size;
    }

    public void setFile_size(Long file_size) {
        this.file_size = file_size;
    }

    public String getFile_url() {
        return file_url;
    }

    public void setFile_url(String file_url) {
        this.file_url = file_url;
    }

    public Mails getMails() {
        return mails;
    }

    public void setMails(Mails mails) {
        this.mails = mails;
    }
}
