package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
