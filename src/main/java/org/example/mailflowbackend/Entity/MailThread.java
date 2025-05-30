package org.example.mailflowbackend.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mail_threads")
public class MailThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL)
    private List<Mails> mails = new ArrayList<>();

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL)
    private List<MailParticipant> participants = new ArrayList<>();

    // Constructors
    public MailThread() {
    }

    public MailThread(String title) {
        this.title = title;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Mails> getMails() {
        return mails;
    }

    public void setMails(List<Mails> mails) {
        this.mails = mails;
    }

    public List<MailParticipant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<MailParticipant> participants) {
        this.participants = participants;
    }
}
