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
    @JoinColumn(name = "user_id")
    private Users users;

    private Boolean isRead = false;

    // Constructors
    public MailParticipant() {
    }

    public MailParticipant(MailThread thread, Users users, Boolean isRead) {
        this.thread = thread;
        this.users = users;
        this.isRead = isRead;
    }

    // Getters and Setters
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

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
