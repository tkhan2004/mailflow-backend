package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.MailThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailThreadRepository extends JpaRepository<MailThread, Long> {
}
