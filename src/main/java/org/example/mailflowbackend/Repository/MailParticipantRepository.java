package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.MailParticipant;
import org.example.mailflowbackend.Entity.MailThread;
import org.example.mailflowbackend.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface MailParticipantRepository extends JpaRepository<MailParticipant, Long> {
    List<MailParticipant> findMailByUsers(Users users);
    
    Optional<MailParticipant> findByThreadIdAndUsersId(Long threadId, Long userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE MailParticipant mp SET mp.isRead = :isRead WHERE mp.id = :id")
    void updateReadStatus(@Param("id") Long id, @Param("isRead") Boolean isRead);
    
    @Modifying
    @Transactional
    @Query("UPDATE MailParticipant mp SET mp.isSpam = :isSpam WHERE mp.id = :id")
    void updateSpamStatus(@Param("id") Long id, @Param("isSpam") Boolean isSpam);

    MailParticipant findByThreadAndUsers(MailThread thread, Users users);
}