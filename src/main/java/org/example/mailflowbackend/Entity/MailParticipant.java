package org.example.mailflowbackend.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mail_participants")
public class MailParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "thread_id")
    private MailThread thread;

    @ManyToOne
    @JoinColumn(name = "mail_id")
    private Mails mails;

    public MailParticipant() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MailThread getThread() {
        return thread;
    }

    public void setThread(MailThread thread) {
        this.thread = thread;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Mails getMails() {
        return mails;
    }

    public void setMails(Mails mails) {
        this.mails = mails;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Boolean getSpam() {
        return isSpam;
    }

    public void setSpam(Boolean spam) {
        isSpam = spam;
    }

    public MailParticipant(Long id, MailThread thread, Users users, Boolean isRead, Boolean isSpam) {
        this.id = id;
        this.thread = thread;
        this.users = users;
        this.isRead = isRead;
        this.isSpam = isSpam;
    }

    public MailParticipant(MailThread thread, Users users, Boolean isRead, Boolean isSpam) {
        this.thread = thread;
        this.users = users;
        this.isRead = isRead;
        this.isSpam = isSpam;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private Boolean isRead = false;
    private Boolean isSpam = false;



    // Constructors





}
