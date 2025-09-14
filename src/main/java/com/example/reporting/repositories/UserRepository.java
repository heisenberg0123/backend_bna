package com.example.reporting.repositories;

import com.example.reporting.entities.User;
import com.example.reporting.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findFirstByEmail(String email);
    User findByUserRole(UserRole userrole);
    User findByProfile(String profile);
}
