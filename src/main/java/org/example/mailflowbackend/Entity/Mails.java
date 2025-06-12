package org.example.mailflowbackend.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name =  "mails")
public class Mails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;
    private String content;

    @Column(name = "create_date")
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "mails", cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "mails",cascade = CascadeType.ALL)
    private List<MailParticipant> mailParticipants = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Users sender;

    public Mails getMails() {
        return mails;
    }

    public void setMails(Mails mails) {
        this.mails = mails;
    }

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Users receiver;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    private MailThread thread;

    public List<MailParticipant> getMailParticipants() {
        return mailParticipants;
    }

    public void setMailParticipants(List<MailParticipant> mailParticipants) {
        this.mailParticipants = mailParticipants;
    }

    @ManyToOne
    @JoinColumn(name = "mails_id")
    private Mails mails;


    // Constructors
    public Mails() {
    }

    public Mails(String subject, String content, LocalDateTime created_at, Users sender, Users receiver, MailThread thread) {
        this.subject = subject;
        this.content = content;
        this.createdAt = created_at;
        this.sender = sender;
        this.receiver = receiver;
        this.thread = thread;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public Users getSender() {
        return sender;
    }

    public void setSender(Users sender) {
        this.sender = sender;
    }

    public Users getReceiver() {
        return receiver;
    }

    public void setReceiver(Users receiver) {
        this.receiver = receiver;
    }

    public MailThread getThread() {
        return thread;
    }

    public void setThread(MailThread thread) {
        this.thread = thread;
    }
}
