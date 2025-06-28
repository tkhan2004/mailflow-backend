package org.example.mailflowbackend.Repository;

import org.example.mailflowbackend.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);


    List<Users> findAllByEmailIn(Collection<String> emails);

    List<Users> findByEmailContainingIgnoreCase(String keyword);
}
