package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.Mails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailRepository extends JpaRepository<Mails, Long> {
}
