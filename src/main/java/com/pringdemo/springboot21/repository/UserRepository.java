package com.pringdemo.springboot21.repository;


import com.pringdemo.springboot21.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Custom query to check if email exists
    boolean existsByEmail(String email);
    
    // Find user by email
    User findByEmail(String email);
}
