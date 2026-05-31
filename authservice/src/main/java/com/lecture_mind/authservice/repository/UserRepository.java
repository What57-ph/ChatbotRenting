package com.lecture_mind.authservice.repository;

import com.lecture_mind.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findUserByRefreshToken(String refreshToken);
}
