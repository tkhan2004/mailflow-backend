package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.MailThread;
import org.example.mailflowbackend.Entity.Mails;
import org.example.mailflowbackend.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MailRepository extends JpaRepository<Mails, Long> {
    List<Mails> findByReceiver(Users users);
    Mails findTopByThreadOrderByCreatedAtDesc(MailThread thread);
}
