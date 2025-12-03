package com.tourism.backend.repository;

import com.tourism.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUserID(Integer userID);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByVerificationToken(String token);
}



